package com.example.kotlinlaravelapirestful.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterResponseObj(
    val message: String,
    val success: Boolean,
    val status: Int = -1,
    val error: String?,
) : Parcelable

data class RegisterResponse(val result: List<RegisterResponseObj>)




