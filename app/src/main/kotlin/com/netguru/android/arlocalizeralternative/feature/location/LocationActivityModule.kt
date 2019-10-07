package com.netguru.android.arlocalizeralternative.feature.location

import android.content.Context
import androidx.lifecycle.ViewModel
import com.netguru.android.arlocalizeralternative.feature.ViewModelKey
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class LocationActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeLocationActivity(): LocationActivity

    @Binds
    @IntoMap
    @ViewModelKey(LocationViewModel::class)
    abstract fun provideLocationViewModel(viewModel: LocationViewModel): ViewModel

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun providesFusedLocationProviderClient(context: Context): FusedLocationProviderClient {
            return FusedLocationProviderClient(context)
        }
    }
}
