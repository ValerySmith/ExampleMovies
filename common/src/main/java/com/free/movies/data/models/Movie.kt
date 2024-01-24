package com.free.movies.data.models

import com.google.gson.annotations.SerializedName

data class Movie(
    val data: Data
)

data class Data(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("page_count")
    val pageCount: Int,
    val next: Int,
    val previous: Int,
    val results: List<MovieData>
)

data class MovieData(
    val id: Int,
    val title: String,
    @SerializedName("short_description")
    val shortDescription: String,
    val description: String,
    @SerializedName("poster_url")
    val posterUrl: String,
    @SerializedName("movie_id")
    val rMovieId: Int,
    @SerializedName("movie_type")
    val movieType: String,
    @SerializedName("title_en")
    val titleEn: String,
    @SerializedName("imdb_rating")
    val imdbRating: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("duration")
    val duration: Long,
    // val attributes: List<List<String>>,
    @SerializedName("audio_tracks")
    val audioTracks: List<AudioTrack>,
)

data class AudioTrack(
    val id: Int,
    @SerializedName("audio_id")
    val rAudioId: Int,
    val title: String
)

