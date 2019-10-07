package com.netguru.arlocalizerview.compass

import com.netguru.arlocalizerview.ARLocalizerDependencyProvider
import com.netguru.arlocalizerview.PermissionManager
import com.netguru.arlocalizerview.arview.ARLocalizerViewModel
import com.netguru.arlocalizerview.arview.IARLocalizerViewModel
import com.netguru.arlocalizerview.location.LocationModule
import com.netguru.arlocalizerview.orientation.OrientationModule
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
