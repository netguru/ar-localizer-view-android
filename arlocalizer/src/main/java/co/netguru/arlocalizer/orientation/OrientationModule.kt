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
    internal fun provideOrientationProvider(sensorManager: SensorManager,
                                            windowManager: WindowManager): OrientationProvider {
        return OrientationProvider(sensorManager, windowManager)
    }

    @Provides
    internal fun provideSensorManager(ARLocalizerDependencyProvider: ARLocalizerDependencyProvider): SensorManager {
        return ARLocalizerDependencyProvider.getSensorsContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    @Provides
    internal fun providesWindowManager(ARLocalizerDependencyProvider: ARLocalizerDependencyProvider): WindowManager {
        return ARLocalizerDependencyProvider.getSensorsContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}