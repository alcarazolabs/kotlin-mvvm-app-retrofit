package com.example.kotlinlaravelapirestful.repository

import com.example.demo.core.Resource
import com.example.kotlinlaravelapirestful.data.model.RegisterResponse
import com.example.kotlinlaravelapirestful.data.remote.UserDataSource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

//Esta clase implementa la interfaz del repositorio "UserRepository"
//implementa sus metodos que buscaran la info en el server
//Esta clase hace la llamada a data/remote/UserDataSource

class UserRepositoryImpl(private val dataSource: UserDataSource) : UserRepository {


    /*
    - Return forma normal
    override suspend fun registerUser(): RegisterResponse {
        return dataSource.registerUser()
    }*/

    //Aprovechando kotlin
   // override suspend fun registerUser(name: String, email: String, password: String): RegisterResponse = dataSource.registerUser(name, email,password)
    //luego se modifica el datasource para que traiga la información desde el webservice

    override suspend fun registerUser(name: String, email: String, password: String) : RegisterResponse = dataSource.registerUser(name, email,password)


}