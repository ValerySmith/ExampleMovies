package com.free.movies.domain.error

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("custom_code")
    val customCode: Int,
    val message: String,
    val details: List<Details>
)
