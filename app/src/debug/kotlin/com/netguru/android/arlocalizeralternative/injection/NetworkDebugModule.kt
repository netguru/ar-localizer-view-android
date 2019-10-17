package com.netguru.android.arlocalizeralternative.injection

import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import javax.inject.Singleton

@Module
class NetworkDebugModule {

    @Provides
    @NetworkInterceptor
    @Singleton
    @IntoSet
    fun provideStethoInterceptor(): Interceptor {
        return StethoInterceptor()
    }
}
