package com.example.kotlinlaravelapirestful.repository

import com.example.kotlinlaravelapirestful.data.model.LoginResponse
import com.example.kotlinlaravelapirestful.data.model.RegisterResponse
import com.example.kotlinlaravelapirestful.data.model.ReportsResponse
import com.example.kotlinlaravelapirestful.data.model.UserInfoResponse
import com.example.primerappmvvmretrofitkotlin.application.AppConstants
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

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

    //Api para registrar reporte, se envia una foto. Se utiliza @Multipart
    @Multipart
    @Headers("Accept: application/json")
    @POST("reports/new")
    suspend fun registerReport(@Header("Authorization") access_token:String,
                               @Part("description") description: RequestBody,
                               @Part photo: MultipartBody.Part
    ) : RegisterResponse

    //api para obtener todos los reportes
    @Headers("Accept: application/json")
    @GET("reports")
    suspend fun getReports(@Header("Authorization") access_token:String): ReportsResponse


}
object RetrofitClient {
    val webservice by lazy { //by lazy solo se inicializara la variable al momento de llamar a webservice. Es en el momento

        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())) //convertir el json del servidor al objeto java/kitlin con gsonbuilder
            .build().create(WebService::class.java)
    }

}