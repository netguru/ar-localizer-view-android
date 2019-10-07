package com.netguru.android.arlocalizeralternative.application

import com.netguru.android.arlocalizeralternative.common.error.RxJavaErrorHandler
import io.reactivex.exceptions.UndeliverableException

class RxJavaErrorHandlerImpl : RxJavaErrorHandler() {

    override fun handleUndeliverableException(undeliverableException: UndeliverableException) {
        /**
         * Crash the app while in debug as undeliverable exception can sometimes be indication of
         * bug's that could have been prevented. Check if disposables/cancelables are set properly
         * on emitter or backup with tryOnError from emitter if that's not possible.
         */
        undeliverableException.printStackTrace()
        uncaught(undeliverableException)
    }

}
