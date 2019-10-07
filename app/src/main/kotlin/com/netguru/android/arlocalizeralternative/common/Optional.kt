package com.netguru.android.arlocalizeralternative.common

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.ofType

/**
 * Optional implementation intended to be used together with Rx2 which doesn't accept nulls.
 */
sealed class Optional<out T : Any> {

    fun toNullable(): T? = when (this) {
        is Some -> value
        is None -> null
    }

    data class Some<out T : Any>(val value: T) : Optional<T>()
    object None : Optional<Nothing>()

    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return an [Optional.Some] describing the
     * result.  Otherwise return an empty [Optional.None].
     */
    inline fun <U : Any> map(f: (T) -> U?): Optional<U> = when (this) {
        is Some -> f(value).toOptional()
        None -> None
    }

    /**
     * If a value is present, apply the provided [Optional]-bearing
     * mapping function to it, return that result, otherwise return an empty
     * [Optional.None].  This method is similar to [Optional.map],
     * but the provided mapper is one whose result is already an [Optional].
     */
    inline fun <U : Any> flatMap(f: (T) -> Optional<U>): Optional<U> = when (this) {
        is Some -> f(value)
        None -> None
    }
}

fun <T : Any> T?.toOptional(): Optional<T> = if (this == null) Optional.None else Optional.Some(
    this
)

fun <T : Any> Observable<Optional<T>>.filterOptionalNone(): Observable<T> =
    this.ofType<Optional.Some<T>>()
        .map { it.toNullable() }

fun <T : Any> Single<Optional<T>>.filterOptionalNone(): Maybe<T> =
    this.filter { it is Optional.Some<T> }
        .map { it.toNullable() }

fun <T : Any> Maybe<Optional<T>>.filterOptionalNone(): Maybe<T> =
    this.filter { it is Optional.Some<T> }
        .map { it.toNullable() }
