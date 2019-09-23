package co.netguru.arlocalizer.location

import co.netguru.arlocalizer.ARLocalizerDependencyProvider
import com.patloew.rxlocation.RxLocation
import dagger.Module
import dagger.Provides

@Module
internal class LocationModule {

    @Provides
    internal fun provideLocationProvider(ARLocalizerDependencyProvider: ARLocalizerDependencyProvider): LocationProvider {
        return LocationProvider(RxLocation(ARLocalizerDependencyProvider.getSensorsContext()))
    }
}