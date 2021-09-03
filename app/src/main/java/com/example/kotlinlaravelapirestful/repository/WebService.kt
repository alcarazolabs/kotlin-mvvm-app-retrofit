package com.example.kotlinlaravelapirestful.repository

import com.example.kotlinlaravelapirestful.data.model.RegisterResponse
import com.example.primerappmvvmretrofitkotlin.application.AppConstants
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface WebService {
    //Mismos métodos que el UserRepository/DataSource

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("register")
    suspend fun registerUser(@Field("name") name: String,
                            @Field("email") email: String,
                            @Field("password")password: String): RegisterResponse

}
object RetrofitClient {

    val webservice by lazy { //by lazy solo se inicializara la variable al momento de llamar a webservice. Es en el momento
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())) //convertir el json del servidor al objeto java/kitlin con gsonbuilder
            .build().create(WebService::class.java)
    }

}