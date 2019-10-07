package com.netguru.arlocalizerview.orientation

import android.hardware.SensorManager
import android.view.WindowManager
import androidx.core.content.getSystemService
import com.netguru.arlocalizerview.ARLocalizerDependencyProvider
import dagger.Module
import dagger.Provides

@Module
internal class OrientationModule {

    @Provides
    internal fun provideOrientationProvider(
        sensorManager: SensorManager,
        windowManager: WindowManager
    ) = OrientationProvider(sensorManager, windowManager)


    @Provides
    internal fun provideSensorManager(arLocalizerDependencyProvider: ARLocalizerDependencyProvider) =
        requireNotNull(arLocalizerDependencyProvider.getSensorsContext().getSystemService<SensorManager>())


    @Provides
    internal fun providesWindowManager(arLocalizerDependencyProvider: ARLocalizerDependencyProvider) =
        requireNotNull(arLocalizerDependencyProvider.getSensorsContext().getSystemService<WindowManager>())
}
