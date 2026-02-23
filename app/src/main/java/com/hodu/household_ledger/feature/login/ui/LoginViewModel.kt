package com.hodu.household_ledger.feature.login.ui

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.hodu.household_ledger.BuildConfig
import com.hodu.household_ledger.core.data.local.TokenManager
import com.hodu.household_ledger.core.data.local.UserManager
import com.hodu.household_ledger.core.data.repository.AuthRepository
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun loginWithKakao(context: Context) {
        if (_uiState.value is LoginUiState.Loading) return
        _uiState.value = LoginUiState.Loading

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                _uiState.value = LoginUiState.Error("카카오 로그인 실패: ${error.message}")
            } else if (token != null) {
                exchangeKakaoToken(token.accessToken)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        _uiState.value = LoginUiState.Idle
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    exchangeKakaoToken(token.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    fun loginWithGoogle(context: Context) {
        if (_uiState.value is LoginUiState.Loading) return
        _uiState.value = LoginUiState.Loading

        val activity = context as? Activity
        if (activity == null) {
            _uiState.value = LoginUiState.Error("구글 로그인 실패: Activity 컨텍스트가 필요합니다")
            return
        }

        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
            BuildConfig.GOOGLE_SERVER_CLIENT_ID
        ).build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        val credentialManager = CredentialManager.create(activity)

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val idToken = googleIdTokenCredential.idToken
                exchangeGoogleToken(idToken)
            } catch (e: GetCredentialCancellationException) {
                _uiState.value = LoginUiState.Idle
            } catch (e: NoCredentialException) {
                _uiState.value = LoginUiState.Error("구글 계정을 찾을 수 없습니다. 기기에 Google 계정을 추가해주세요.")
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("구글 로그인 실패: ${e.message}")
            }
        }
    }

    private fun exchangeKakaoToken(kakaoAccessToken: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.kakaoLogin(kakaoAccessToken)
                TokenManager.saveToken(response.accessToken)
                UserManager.save(
                    id = response.user.id,
                    nickname = response.user.nickname,
                    profileImageUrl = response.user.profileImageUrl,
                    provider = "kakao"
                )
                _uiState.value = LoginUiState.Success
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("서버 인증 실패: ${e.message}")
            }
        }
    }

    private fun exchangeGoogleToken(idToken: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.googleLogin(idToken)
                TokenManager.saveToken(response.accessToken)
                UserManager.save(
                    id = response.user.id,
                    nickname = response.user.nickname,
                    profileImageUrl = response.user.profileImageUrl,
                    provider = "google"
                )
                _uiState.value = LoginUiState.Success
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("서버 인증 실패: ${e.message}")
            }
        }
    }
}
