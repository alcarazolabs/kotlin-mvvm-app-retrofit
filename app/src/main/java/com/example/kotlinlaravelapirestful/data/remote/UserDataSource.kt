package com.example.kotlinlaravelapirestful.data.remote


import com.example.demo.core.Resource
import com.example.kotlinlaravelapirestful.data.model.LoginResponse
import com.example.kotlinlaravelapirestful.data.model.RegisterResponse
import com.example.kotlinlaravelapirestful.data.model.RegisterResponseObj
import com.example.kotlinlaravelapirestful.repository.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

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
}