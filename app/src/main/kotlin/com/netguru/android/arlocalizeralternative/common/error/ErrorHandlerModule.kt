package com.netguru.android.arlocalizeralternative.common.error

import com.netguru.android.arlocalizeralternative.application.RxJavaErrorHandlerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ErrorHandlerModule {

    @Singleton
    @Provides
    fun rxJavaErrorHandler(): RxJavaErrorHandler =
        RxJavaErrorHandlerImpl()
}
