package com.free.movies.data

import com.free.movies.data.models.ForProxy
import com.free.movies.data.models.Movie
import com.free.movies.data.models.MovieData
import com.free.movies.data.models.MovieDetail
import com.free.movies.data.models.Streams
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface Service {

    companion object {
        const val R_MOVIE_ID = "movie_id"
        const val R_AUDIO_ID = "audio_id"
        const val MOVIE_ID = "movie_id"
        const val HEADER = "X-API-KEY"
    }

    @GET("/api/v1/movies")
    suspend fun fetchAllMovies(
        @Header(HEADER)
        header: String,
        @Query("movie_type")
        movieType: String,
        @Query("order_by")
        orderBy: String?,
        @Query("search")
        search: String?,
        @Query("page")
        page: Int,
        @Query("page_size")
        pageSize: Int
    ): Response<Movie>

    @GET("api/v1/movies/{$MOVIE_ID}")
    suspend fun fetchMovie(
        @Header(HEADER)
        header: String,
        @Path(MOVIE_ID)
        movieId: Int
    ): Response<MovieDetail>

    @GET("/api/v1/movies/film/{$R_MOVIE_ID}/audio/{$R_AUDIO_ID}/stream/request")
    suspend fun fetchProxyData(
        @Header(HEADER)
        header: String,
        @Path(R_MOVIE_ID)
        movieId: Int,
        @Path(R_AUDIO_ID)
        audioId: Int
    ): Response<ForProxy>


    @POST("api/v1/movies/film/stream/parse/")
    suspend fun fetchStream(
        @Header(HEADER)
        header: String,
        @Body data: RequestBody?
    ): Response<Streams>

    @GET
    suspend fun redirect(
        @Url url: String?,
        @Header(HEADER)
        header: String,
    ): Response<String>

    @FormUrlEncoded
    @POST
    suspend fun fetchData(
        @Url url: String?,
        @HeaderMap headerMap: Map<String, String>?,
        @Field("id") id: Int?,
        @Field("translator_id") translatorId: Int?,
        @Field("action") action: String?
    ): Response<ResponseBody>
}