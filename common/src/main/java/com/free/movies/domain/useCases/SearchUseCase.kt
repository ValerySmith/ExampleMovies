package com.free.movies.domain.useCases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.free.movies.data.models.MovieData
import com.free.movies.data.repository.MovieRepository
import com.free.movies.data.datasource.MoviePagingSource
import com.free.movies.utils.HEADER
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUseCase @Inject constructor(private val repository: MovieRepository) {

    fun fetchSearch(
        movieType: String,
        query: String
    ): Flow<PagingData<MovieData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                prefetchDistance = 5,
                initialLoadSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MoviePagingSource(
                    header = HEADER, movieType = movieType, repo = repository, search = query
                )
            }).flow
    }
}