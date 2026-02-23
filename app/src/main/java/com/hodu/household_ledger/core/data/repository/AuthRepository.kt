package com.hodu.household_ledger.core.data.repository

import com.hodu.household_ledger.core.network.ApiClient
import com.hodu.household_ledger.core.network.dto.GoogleLoginRequest
import com.hodu.household_ledger.core.network.dto.KakaoLoginRequest
import com.hodu.household_ledger.core.network.dto.LoginResponse
import com.hodu.household_ledger.core.network.dto.UserDto

class AuthRepository {
    private val authApi = ApiClient.authApi

    suspend fun kakaoLogin(kakaoAccessToken: String): LoginResponse {
        return authApi.kakaoLogin(KakaoLoginRequest(accessToken = kakaoAccessToken))
    }

    suspend fun googleLogin(idToken: String): LoginResponse {
        return authApi.googleLogin(GoogleLoginRequest(idToken = idToken))
    }

    suspend fun getMe(): UserDto {
        return authApi.getMe()
    }

    suspend fun deleteAccount() {
        authApi.deleteAccount()
    }
}
