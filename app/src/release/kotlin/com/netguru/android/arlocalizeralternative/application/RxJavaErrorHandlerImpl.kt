package com.netguru.android.arlocalizeralternative.application

import com.netguru.android.arlocalizeralternative.common.error.RxJavaErrorHandler
import io.reactivex.exceptions.UndeliverableException
import timber.log.Timber

class RxJavaErrorHandlerImpl : RxJavaErrorHandler() {

    override fun handleUndeliverableException(undeliverableException: UndeliverableException) {
        Timber.e(undeliverableException)
    }

}
