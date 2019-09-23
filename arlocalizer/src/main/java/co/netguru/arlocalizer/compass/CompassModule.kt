package co.netguru.arlocalizer.compass

import co.netguru.arlocalizer.ARLocalizerDependencyProvider
import co.netguru.arlocalizer.PermissionManager
import co.netguru.arlocalizer.arview.ARLocalizerViewModel
import co.netguru.arlocalizer.arview.IARLocalizerViewModel
import co.netguru.arlocalizer.location.LocationModule
import co.netguru.arlocalizer.orientation.OrientationModule
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(
    includes = [
        LocationModule::class,
        OrientationModule::class
    ]
)
internal abstract class CompassModule {

    @Binds
    abstract fun provideARLocalizerPresenter(viewModel: ARLocalizerViewModel): IARLocalizerViewModel

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun providesPermissionManager(arLocalizerDependencyProvider: ARLocalizerDependencyProvider): PermissionManager {
            return PermissionManager(
                arLocalizerDependencyProvider.getPermissionActivity()
            )
        }
    }
}
