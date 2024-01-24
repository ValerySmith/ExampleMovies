package com.free.movies.domain.useCases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.free.movies.data.NetworkResult
import com.free.movies.data.models.MovieType
import com.free.movies.data.models.MovieData
import com.free.movies.data.repository.MovieRepository
import com.free.movies.data.datasource.MoviePagingSource
import com.free.movies.domain.extentions.Result
import com.free.movies.domain.models.mapStreams
import com.free.movies.utils.HEADER
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import javax.inject.Inject

class MovieUseCase @Inject constructor(private val repository: MovieRepository) {

    fun fetchAllMovies(
        movieType: String = MovieType.MOVIE.type
    ): Flow<PagingData<MovieData>> {
        return Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 40, initialLoadSize = 20),
            pagingSourceFactory = {
                MoviePagingSource(
                    header = HEADER, movieType = movieType, repo = repository
                )
            }).flow
    }

    suspend fun fetchMovie(movieId: Int) =
        when (val result = repository.fetchMovie(HEADER, movieId)) {
            is NetworkResult.Success -> {
                Result.success(result.data)
            }

            else -> {
                Result.failure(result.errorType)
            }
        }

    suspend fun fetchProxyData(
        movieId: Int,
        audioId: Int
    ) = when (val result =
        repository.fetchProxyData(
            header = HEADER,
            movieId = movieId,
            audioId = audioId
        )) {
        is NetworkResult.Success -> {
            Result.success(result.data)
        }

        else -> {
            Result.failure(result.errorType)
        }
    }

    suspend fun fetchJson(
        url: String?,
        headerMap: Map<String, String>?,
        id: Int?,
        translatorId: Int?,
        action: String?
    ) = when (val result = repository.fetchData(
        url = url,
        headerMap = headerMap,
        id = id,
        translatorId = translatorId,
        action = action
    )) {
        is NetworkResult.Success -> {
            Result.success(result.data)
        }

        else -> {
            Result.failure(result.errorType)
        }

    }

    suspend fun fetchStreams(data: RequestBody?) =
        when (val result = repository.fetchStream(header = HEADER, data = data)) {
            is NetworkResult.Success -> {
                Result.success(mapStreams(result.data?.data?.streams))
            }

            else -> {
                Result.failure(result.errorType)
            }
        }

    suspend fun redirect(url: String?) =
        Result.success(repository.redirect(url, HEADER).replace("http", "https"))
}