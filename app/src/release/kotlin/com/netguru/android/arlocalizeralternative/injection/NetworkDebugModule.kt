package com.netguru.android.arlocalizeralternative.injection

import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import java.util.*


@Module
class NetworkDebugModule {

    @Provides
    @NetworkInterceptor
    fun provideEmptyInterceptorSet(): Set<Interceptor> {
        return Collections.emptySet()
    }
}
