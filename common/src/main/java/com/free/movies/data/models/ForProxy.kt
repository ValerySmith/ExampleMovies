package com.free.movies.data.models

import com.google.gson.annotations.SerializedName

data class ForProxy(
    val data: ForProxyData
)

data class ForProxyData(
    @SerializedName("data")
    val innerData: InnerData,
    val headers: HashMap<String, String>,
    val url: String,
    val method: String
)

data class InnerData(
    val id: Int,
    @SerializedName("translator_id")
    val translatorId: Int,
    val action: String
)

