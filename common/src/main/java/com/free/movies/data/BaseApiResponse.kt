package com.free.movies.data

import android.util.Log
import com.free.movies.domain.error.CustomError
import com.free.movies.domain.error.ErrorResponse
import com.free.movies.domain.error.NetworkError
import com.free.movies.domain.error.UnknownError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.net.UnknownHostException

abstract class BaseApiResponse {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            return if (response.isSuccessful) {
                val body = response.body()
                NetworkResult.Success(body)
            } else {
                val type = object : TypeToken<ErrorResponse>() {}.type
                val errorResponse: ErrorResponse? =
                    Gson().fromJson(response.errorBody()?.charStream(), type)
                error(parseError(errorResponse))
            }
        } catch (e: Exception) {
            Log.e("TEST", "Error - ${e.stackTraceToString()}")
            return when (e) {
                is UnknownHostException -> {
                    error(NetworkError)
                }

                else -> {
                    error(UnknownError)
                }
            }
        }
    }

    private fun parseError(error: ErrorResponse?) {
        if (error?.customCode != 0)
            CustomError(error?.details?.first()?.message.toString())
        else
            UnknownError
    }

    private fun <T> error(error: ErrorType): NetworkResult<T> =
        NetworkResult.Error(error)

    private companion object {
        private const val ERROR_VALIDATION = 400
        private const val ERROR_UNAUTHORIZED = 401
        private const val ERROR_INVALID_PARAMETERS = 422
        private const val ERROR_SERVER = 500
    }
}