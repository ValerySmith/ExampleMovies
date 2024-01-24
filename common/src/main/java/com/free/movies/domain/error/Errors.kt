package com.free.movies.domain.error

import com.free.movies.data.ErrorType

class CustomError(override val message: String): ErrorType()

object UnknownError : ErrorType()

object NetworkError : ErrorType()

object EmptyLinkError: ErrorType()

class ServerError(override val message: String) : ErrorType()