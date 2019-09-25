package co.netguru.arlocalizer.orientation

import android.content.Context
import android.hardware.SensorManager
import android.view.WindowManager
import co.netguru.arlocalizer.ARLocalizerDependencyProvider
import dagger.Module
import dagger.Provides

@Module
internal class OrientationModule {

    @Provides
    internal fun provideOrientationProvider(
        sensorManager: SensorManager?,
        windowManager: WindowManager?
    ) = OrientationProvider(sensorManager, windowManager)


    @Provides
    internal fun provideSensorManager(arLocalizerDependencyProvider: ARLocalizerDependencyProvider) =
        arLocalizerDependencyProvider.getSensorsContext().getSystemService(Context.SENSOR_SERVICE) as? SensorManager


    @Provides
    internal fun providesWindowManager(arLocalizerDependencyProvider: ARLocalizerDependencyProvider) =
        arLocalizerDependencyProvider.getSensorsContext().getSystemService(Context.WINDOW_SERVICE) as? WindowManager
}
