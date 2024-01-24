package com.free.movies.data.models

import com.google.gson.annotations.SerializedName

data class MovieDetail(@SerializedName("data") val movie: MovieData)
