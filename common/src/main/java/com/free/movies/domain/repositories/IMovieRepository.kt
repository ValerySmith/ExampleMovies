package com.free.movies.domain.repositories

import com.free.movies.data.NetworkResult
import com.free.movies.data.models.ForProxy
import com.free.movies.data.models.Movie
import com.free.movies.data.models.MovieData
import com.free.movies.data.models.MovieDetail
import com.free.movies.data.models.Streams
import okhttp3.RequestBody
import okhttp3.ResponseBody

interface IMovieRepository {
    suspend fun fetchAllMovies(
        header: String,
        movieType: String,
        orderBy: String?,
        search: String?,
        page: Int,
        pageSize: Int = 25
    ): NetworkResult<Movie>

    suspend fun fetchMovie(header: String, movieId: Int): NetworkResult<MovieDetail>

    suspend fun fetchProxyData(
        header: String,
        movieId: Int,
        audioId: Int,
    ): NetworkResult<ForProxy>

    suspend fun fetchStream(
        header: String,
        data: RequestBody?
    ): NetworkResult<Streams>

    suspend fun redirect(url: String?, header: String): String

    suspend fun fetchData(
        url: String?,
        headerMap: Map<String, String>?,
        id: Int?,
        translatorId: Int?,
        action: String?
    ): NetworkResult<ResponseBody>
}

