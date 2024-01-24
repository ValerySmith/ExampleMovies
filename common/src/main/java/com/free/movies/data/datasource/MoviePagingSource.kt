package com.free.movies.data.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.free.movies.data.NetworkResult
import com.free.movies.data.models.MovieData
import com.free.movies.data.repository.MovieRepository
import java.io.IOException

class MoviePagingSource(
    private val header: String,
    private val movieType: String,
    private val orderBy: String? = null,
    private val search: String? = null,
    private val repo: MovieRepository
) : PagingSource<Int, MovieData>() {

    override fun getRefreshKey(state: PagingState<Int, MovieData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override val keyReuseSupported: Boolean
        get() = false

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieData> {
        return try {
            val currentPage = params.key ?: 1
            val result =
                repo.fetchAllMovies(header, movieType, orderBy, search, currentPage)
            when (result) {
                is NetworkResult.Success -> {
                    val data = result.data?.data?.results ?: emptyList()
                    LoadResult.Page(
                        data = data,
                        prevKey = if (currentPage == 1) null else currentPage.minus(1),
                        nextKey = if (data.isEmpty()) null else result.data?.data?.next?.plus(1)
                    )
                }

                is NetworkResult.Error -> {
                    val throwable = Throwable(message = result.errorType?.message)
                    Log.e("TEST", "Error - ${throwable.message}")
                    LoadResult.Error(throwable)
                }
            }
        } catch (e: IOException) {
            Log.e("Paging Catch - ", "${e.message}")
            LoadResult.Error(e)
        }
    }
}