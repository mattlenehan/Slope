package com.example.slope.repository

import com.example.networking.util.ApiResult
import retrofit2.Response
import retrofit2.Retrofit

abstract class BaseRepository(protected val retrofit: Retrofit) {

    protected suspend fun <T> getResponse(
        request: suspend () -> Response<T>,
        defaultErrorMessage: String,
        apiErrorParser: (Response<T>, String) -> ApiResult<T> = ::defaultErrorParser,
    ): ApiResult<T> {
        return try {
            val result = request.invoke()
            return if (result.isSuccessful) {
                ApiResult.success(result.body())
            } else {
                return apiErrorParser(result, defaultErrorMessage)
            }
        } catch (e: Throwable) {
            ApiResult.error(e.message ?: "Unknown Error", null)
        }
    }

    private fun <T> defaultErrorParser(response: Response<T>, defaultErrorMessage: String):
            ApiResult<T> {
        return ApiResult.error(response.message() ?: defaultErrorMessage, null)
    }
}