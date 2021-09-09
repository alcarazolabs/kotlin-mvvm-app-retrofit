package com.example.kotlinlaravelapirestful.data.remote


import com.example.demo.core.Resource
import com.example.kotlinlaravelapirestful.data.model.*
import com.example.kotlinlaravelapirestful.repository.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File

//Codigo para obtener la data del servidor con retrofit y se la data al repositorio

//class UserDataSource() {
//obtener intancia del webservice
class UserDataSource(private val webService: WebService) {
    /*
    fun registerUser() : RegisterResponse{
        return RegisterResponse()
    }*/


    suspend fun registerUser(name: String, email:String, password:String) : RegisterResponse {
    return webService.registerUser(name, email, password)
    }

    suspend fun loginUser(email:String, password: String) : LoginResponse = webService.loginUser(email, password)

    suspend fun userInfo(access_token:String) : UserInfoResponse = webService.userInfo(access_token)

    suspend fun registerReport(access_token: String, description: RequestBody, photo: MultipartBody.Part) : RegisterResponse = webService.registerReport(access_token, description, photo)

    suspend fun getReports(access_token:String) : ReportsResponse = webService.getReports(access_token)
}