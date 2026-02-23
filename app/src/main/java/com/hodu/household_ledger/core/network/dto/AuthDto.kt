package com.hodu.household_ledger.core.network.dto

data class KakaoLoginRequest(
    val accessToken: String
)

data class GoogleLoginRequest(
    val idToken: String
)

data class LoginResponse(
    val accessToken: String,
    val user: UserDto
)

data class UserDto(
    val id: Long,
    val kakaoId: Long?,
    val googleId: String?,
    val nickname: String?,
    val profileImageUrl: String?
)
