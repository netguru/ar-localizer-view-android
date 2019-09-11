package co.netguru.android.arlocalizeralternative.feature.location

import android.app.Application
import com.patloew.rxlocation.RxLocation
import dagger.Module
import dagger.Provides

@Module
class LocationModule {

    @Provides
    internal fun provideLocationProvider(context: Application): LocationProvider {
        return LocationProvider(RxLocation(context))
    }
}