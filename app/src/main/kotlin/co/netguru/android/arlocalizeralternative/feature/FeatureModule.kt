package co.netguru.android.arlocalizeralternative.feature

import androidx.lifecycle.ViewModelProvider
import co.netguru.android.arlocalizeralternative.feature.camera.CameraModule
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module(
    includes = [
        CameraModule::class
    ]
)
abstract class FeatureModule {

    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
}
