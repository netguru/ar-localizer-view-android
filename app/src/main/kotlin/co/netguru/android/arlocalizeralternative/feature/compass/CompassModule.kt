package co.netguru.android.arlocalizeralternative.feature.compass

import androidx.lifecycle.ViewModel
import co.netguru.android.arlocalizeralternative.feature.ViewModelKey
import co.netguru.android.arlocalizeralternative.feature.location.LocationModule
import co.netguru.android.arlocalizeralternative.feature.orientation.OrientationModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(
    includes = [
        LocationModule::class,
        OrientationModule::class
    ]
)
abstract class CompassModule {

    @Binds
    @IntoMap
    @ViewModelKey(CompassViewModel::class)
    abstract fun provideCompassViewModel(viewModel: CompassViewModel): ViewModel
}