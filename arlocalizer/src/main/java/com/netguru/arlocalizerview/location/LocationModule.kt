package com.netguru.arlocalizerview.location

import com.netguru.arlocalizerview.ARLocalizerDependencyProvider
import com.patloew.rxlocation.RxLocation
import dagger.Module
import dagger.Provides

@Module
internal class LocationModule {

    @Provides
    internal fun provideLocationProvider(arLocalizerDependencyProvider: ARLocalizerDependencyProvider) =
        LocationProvider(RxLocation(arLocalizerDependencyProvider.getSensorsContext()))
}
