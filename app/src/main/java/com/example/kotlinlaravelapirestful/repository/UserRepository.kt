package com.example.kotlinlaravelapirestful.repository

import com.example.kotlinlaravelapirestful.data.model.LoginResponse
import com.example.kotlinlaravelapirestful.data.model.RegisterResponse
import com.example.kotlinlaravelapirestful.data.model.UserInfoResponse

//Esta interface tiene los mismos métodos que el dataSource

interface UserRepository {

  suspend  fun registerUser(name:String,email:String,password:String): RegisterResponse
  suspend fun loginUser(email: String, password: String): LoginResponse
  suspend fun userInfo(access_token:String) : UserInfoResponse
  suspend fun saveAuthToken(token: String)
}