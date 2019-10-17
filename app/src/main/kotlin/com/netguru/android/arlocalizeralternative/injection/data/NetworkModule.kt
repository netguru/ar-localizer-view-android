package com.netguru.android.arlocalizeralternative.injection.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.netguru.android.arlocalizeralternative.injection.NetworkInterceptor
import com.netguru.android.arlocalizeralternative.injection.NetworkDebugModule
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module(includes = [NetworkDebugModule::class])
class NetworkModule {

    @Provides
    fun provideOkHttp(@NetworkInterceptor networkInterceptors: Set<@JvmSuppressWildcards Interceptor>)
            : OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptors(networkInterceptors)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .create()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    internal fun provideRxJavaCallAdapterFactory(): CallAdapter.Factory =
        RxJava2CallAdapterFactory.create()

    @Provides
    @Singleton
    internal fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        rxJavaCallAdapterFactory: CallAdapter.Factory,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    private fun OkHttpClient.Builder.addNetworkInterceptors(interceptors: Collection<Interceptor>)
            : OkHttpClient.Builder =
        apply {
            interceptors.forEach { addNetworkInterceptor(it) }
        }

    companion object {
        private const val TIMEOUT = 25L
        private const val BASE_URL = "https://overpass-api.de/api/"
    }
}


