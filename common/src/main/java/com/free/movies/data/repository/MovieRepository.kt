package com.free.movies.data.repository

import com.free.movies.data.BaseApiResponse
import com.free.movies.data.NetworkResult
import com.free.movies.data.Service
import com.free.movies.data.models.ForProxy
import com.free.movies.data.models.Movie
import com.free.movies.data.models.MovieData
import com.free.movies.data.models.MovieDetail
import com.free.movies.data.models.Streams
import com.free.movies.domain.repositories.IMovieRepository
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val apiService: Service
) : BaseApiResponse(),
    IMovieRepository {

    override suspend fun fetchAllMovies(
        header: String,
        movieType: String,
        orderBy: String?,
        search: String?,
        page: Int,
        pageSize: Int
    ): NetworkResult<Movie> =
        safeApiCall {
            apiService.fetchAllMovies(
                header,
                movieType,
                orderBy,
                search,
                page,
                pageSize
            )
        }

    override suspend fun fetchMovie(header: String, movieId: Int): NetworkResult<MovieDetail> =
        safeApiCall {
            apiService.fetchMovie(header, movieId)
        }

    override suspend fun fetchProxyData(
        header: String,
        movieId: Int,
        audioId: Int,
    ): NetworkResult<ForProxy> = safeApiCall {
        apiService.fetchProxyData(header, movieId, audioId)
    }

    override suspend fun fetchStream(
        header: String,
        data: RequestBody?
    ): NetworkResult<Streams> = safeApiCall {
        apiService.fetchStream(header, data)
    }

    override suspend fun redirect(url: String?, header: String): String {
        return apiService.redirect(url, header).raw().request.url.toString()
    }

    override suspend fun fetchData(
        url: String?,
        headerMap: Map<String, String>?,
        id: Int?,
        translatorId: Int?,
        action: String?
    ): NetworkResult<ResponseBody> =
        safeApiCall {
            apiService.fetchData(url, headerMap, id, translatorId, action)
        }
}