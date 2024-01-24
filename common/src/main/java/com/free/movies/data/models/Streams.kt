package com.free.movies.data.models

data class Streams(
    val data: DataStreams
)

data class DataStreams(val streams: List<Stream>)

data class Stream(val quality: String, val streams: Map<String, String>)
