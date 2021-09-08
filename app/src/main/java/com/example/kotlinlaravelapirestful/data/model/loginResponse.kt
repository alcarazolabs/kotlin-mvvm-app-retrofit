package com.example.kotlinlaravelapirestful.data.model

data class LoginResponseObj(
    val user: User? = null,
    val access_token: String?,
    val token_type: String?,
    val message: String,
    val success: Boolean,
    val status: Int = -1,
)

data class LoginResponse(val result: List<LoginResponseObj>)
