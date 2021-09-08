package com.example.kotlinlaravelapirestful.repository

import androidx.lifecycle.lifecycleScope
import com.example.kotlinlaravelapirestful.core.UserPreferences
import com.example.kotlinlaravelapirestful.data.model.LoginResponse
import com.example.kotlinlaravelapirestful.data.model.RegisterResponse
import com.example.kotlinlaravelapirestful.data.model.UserInfoResponse
import com.example.primerappmvvmretrofitkotlin.application.AppConstants
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import androidx.lifecycle.lifecycleScope
interface WebService {
    //Mismos métodos que el UserRepository/DataSource

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("register")
    suspend fun registerUser(@Field("name") name: String,
                            @Field("email") email: String,
                            @Field("password")password: String): RegisterResponse

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("login")
    suspend fun loginUser(@Field("email") email: String,
                          @Field("password")password: String): LoginResponse

    @Headers("Accept: application/json")
    @GET("userinfo")
    suspend fun userInfo(@Header("Authorization") access_token:String): UserInfoResponse
}
object RetrofitClient {
    val webservice by lazy { //by lazy solo se inicializara la variable al momento de llamar a webservice. Es en el momento

        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())) //convertir el json del servidor al objeto java/kitlin con gsonbuilder
            .build().create(WebService::class.java)
    }

}