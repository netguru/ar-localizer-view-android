package co.netguru.arlocalizer.location

import co.netguru.arlocalizer.ARLocalizerDependencyProvider
import com.patloew.rxlocation.RxLocation
import dagger.Module
import dagger.Provides

@Module
internal class LocationModule {

    @Provides
    internal fun provideLocationProvider(arLocalizerDependencyProvider: ARLocalizerDependencyProvider) =
        LocationProvider(RxLocation(arLocalizerDependencyProvider.getSensorsContext()))
}
