package co.netguru.android.arlocalizeralternative.feature.camera

import co.netguru.android.arlocalizeralternative.feature.compass.CompassModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(
    includes =
        [CompassModule::class]
)
abstract class CameraModule {

    @ContributesAndroidInjector
    abstract fun contributeActivity(): CameraActivity
}
