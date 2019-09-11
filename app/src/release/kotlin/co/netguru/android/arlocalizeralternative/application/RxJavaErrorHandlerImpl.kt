package co.netguru.android.arlocalizeralternative.application

import co.netguru.android.arlocalizeralternative.common.error.RxJavaErrorHandler
import io.reactivex.exceptions.UndeliverableException
import timber.log.Timber

class RxJavaErrorHandlerImpl : RxJavaErrorHandler() {

    override fun handleUndeliverableException(undeliverableException: UndeliverableException) {
        //TODO - decide whether this should be logged and passed or not to used crash reporter
        //often occurring might be indication of some problem in library or our codebase but definitely shouldn't crash the app
        //Crashlytics.logException(undeliverableException)
        Timber.e(undeliverableException)
    }

}
