package com.netguru.android.arlocalizeralternative.application

import com.netguru.android.arlocalizeralternative.common.error.RxJavaErrorHandler
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.reactivex.plugins.RxJavaPlugins
import javax.inject.Inject

class App : DaggerApplication() {

    @Inject
    lateinit var debugMetricsHelper: DebugMetricsHelper

    @Inject
    lateinit var rxJavaErrorHandler: RxJavaErrorHandler

    override fun onCreate() {
        super.onCreate()
        debugMetricsHelper.init(this)
        RxJavaPlugins.setErrorHandler(rxJavaErrorHandler)
    }

    override fun applicationInjector(): AndroidInjector<App> = DaggerApplicationComponent.factory()
        .create(this)
}
