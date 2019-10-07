package com.netguru.android.arlocalizeralternative.common.error

import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer

/**
 * [RxJava2 error handling](https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling)
 */
abstract class RxJavaErrorHandler : Consumer<Throwable> {

    override fun accept(throwable: Throwable) = when (throwable) {
        is UndeliverableException -> {
            //we log such exceptions but avoid app crash for release as we can't do much in such case.
            handleUndeliverableException(throwable)
        }
        else -> {
            //we crash the app else - this is a bug
            throwable.printStackTrace()
            uncaught(throwable)
        }
    }

    /**
     * Something thrown error after stream finished.
     * If this happens often it can be indication of some problem in library or our codebase.
     * Make sure that source sets disposable/cancellable while creating stream.
     * You can also use tryOnError if the wrapped data source doesn't provide good way to cancel emissions.
     */
    abstract fun handleUndeliverableException(undeliverableException: UndeliverableException)

    protected fun uncaught(throwable: Throwable) {
        val currentThread = Thread.currentThread()
        val handler = currentThread.uncaughtExceptionHandler
        handler.uncaughtException(currentThread, throwable)
    }
}
