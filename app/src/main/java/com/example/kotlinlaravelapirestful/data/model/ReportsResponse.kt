package com.example.kotlinlaravelapirestful.data.model

data class ReportsResponseObj(
    val id: Int = -1,
    val description: String,
    val photo: String,
    val user_id: Int = -1,
    val created_at: String,
    val updated_at: String,
)

data class ReportsResponse(val result : List<ReportsResponseObj> = listOf())