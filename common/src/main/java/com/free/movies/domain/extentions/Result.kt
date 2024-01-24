package com.free.movies.domain.extentions

sealed class Result<out S, out F> {

    data class Success<out T> internal constructor(val data: T) : Result<T, Nothing>()

    data class Failure<out E> internal constructor(val failure: E) : Result<Nothing, E>()

    companion object {

        fun <S> success(data: S): Success<S> = Success(data)

        fun <F> failure(error: F): Failure<F> = Failure(error)
    }
}

fun <S, F> Result<S, F>.ifSuccess(action: (S) -> Unit): Result<S, F> = apply {
    if (this is Result.Success) action(data)
}

fun <S, F> Result<S, F>.ifFailure(action: (F) -> Unit): Result<S, F> = apply {
    if (this is Result.Failure) action(failure)
}

fun <S, T, F> Result<S, F>.map(action: (S) -> T): Result<T, F> = when (this) {
    is Result.Failure -> this
    is Result.Success -> Result.success(action(data))
}

fun <S, T, F> Result<S, F>.mapFailure(action: (F) -> T): Result<S, T> = when (this) {
    is Result.Failure -> Result.failure(action(failure))
    is Result.Success -> this
}

fun <S, F> Result<S, F>.returnIfFailure(action: () -> S): Result<S, F> = when (this) {
    is Result.Failure -> Result.success(action())
    is Result.Success -> this
}

fun <S, F : Throwable> Result<S, F>.getOrThrow(): S = when (this) {
    is Result.Failure -> throw failure
    is Result.Success -> data
}

fun <S, S2, F> Result<S, F>.flatMap(action: (S) -> Result< S2,F>): Result<S2, F> = when (this) {
    is Result.Failure -> this
    is Result.Success -> action(data)
}