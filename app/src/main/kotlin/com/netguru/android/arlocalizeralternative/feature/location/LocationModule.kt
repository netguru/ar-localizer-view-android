package com.netguru.android.arlocalizeralternative.feature.location

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.netguru.android.arlocalizeralternative.feature.location.data.LocationApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


@Suppress("unused")
@Module
class LocationModule {

    @Provides
    fun providesLocationApi(retrofit: Retrofit): LocationApi {
        return retrofit.create(LocationApi::class.java)
    }

    @Provides
    fun providesFusedLocationProviderClient(context: Context): FusedLocationProviderClient {
        return FusedLocationProviderClient(context)
    }
}
