package com.hodu.household_ledger.core.network.api

import com.hodu.household_ledger.core.network.dto.GoogleLoginRequest
import com.hodu.household_ledger.core.network.dto.KakaoLoginRequest
import com.hodu.household_ledger.core.network.dto.LoginResponse
import com.hodu.household_ledger.core.network.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/kakao")
    suspend fun kakaoLogin(@Body request: KakaoLoginRequest): LoginResponse

    @POST("auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): LoginResponse

    @GET("auth/me")
    suspend fun getMe(): UserDto

    @DELETE("auth/me")
    suspend fun deleteAccount(): Response<Unit>
}
