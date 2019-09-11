package co.netguru.android.arlocalizeralternative.feature.orientation

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import android.view.WindowManager
import dagger.Module
import dagger.Provides

@Module
class OrientationModule {

    @Provides
    internal fun provideOrientationProvider(context: Application): OrientationProvider {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return OrientationProvider(sensorManager, windowManager)
    }
}