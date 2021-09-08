package com.example.kotlinlaravelapirestful.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


//transforma datos json a un objeto
@Parcelize
data class User (
    val id: Int = -1,
    val name: String,
    val email: String,
    val email_verified_at: String?,
    val created_at: String,
    val updated_at: String
 ): Parcelable

data class UserInfoObj(
    val user: User
)

data class UserInfoResponse(val result: List<UserInfoObj>)
