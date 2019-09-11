package co.netguru.android.arlocalizeralternative.feature.orientation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.Surface.*
import android.view.WindowManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class OrientationProvider @Inject constructor(private val sensorManager: SensorManager,
private val windowManager: WindowManager) {

    private val rotationVector = FloatArray(3)
    private var azimuth: Float = 0f
    private var alpha = 0f
    private var lastCos = 0f
    private var lastSin = 0f

    private var orientationPublishSubject: PublishSubject<OrientationData>? = null

    private var sensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            when (sensor?.type) {
                Sensor.TYPE_ROTATION_VECTOR -> when (accuracy) {
                    SensorManager.SENSOR_STATUS_ACCURACY_LOW -> Log.d("ACCURACY", "low")
                    SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> Log.d("ACCURACY", "medium")
                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> Log.d("ACCURACY", "high")
                }
                else -> {
                }
            }
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) handleSensorEvent(event)
        }
    }

    companion object {
        private const val SENSORS_ERROR = "Sensors are not available"
    }

    private fun isSensorAvailable(sensorType: Int): Boolean {
        return sensorManager.getDefaultSensor(sensorType) != null
    }

    fun startSensorObservation() {
        orientationPublishSubject = PublishSubject.create()
        if (!isSensorAvailable(Sensor.TYPE_ROTATION_VECTOR)) {
            orientationPublishSubject?.onError(Throwable(SENSORS_ERROR))
            return
        }
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        sensorManager.registerListener(sensorEventListener, rotationSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stopSensorObservation() {
        sensorManager.unregisterListener(sensorEventListener)
    }

    fun getSensorUpdates(): Flowable<OrientationData>? {
        return orientationPublishSubject?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.toFlowable(BackpressureStrategy.LATEST)
    }

    private fun handleSensorEvent(sensorEvent: SensorEvent) {
        synchronized(this) {
            if (sensorEvent.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                rotationVector[0] = rotationVector[0] + alpha * (sensorEvent.values[0] - rotationVector[0])
                rotationVector[1] = rotationVector[1] + alpha * (sensorEvent.values[1] - rotationVector[1])
                rotationVector[2] = rotationVector[2] + alpha * (sensorEvent.values[2] - rotationVector[2])
            }

            val rotationMatrix = FloatArray(9)
            val orientation = FloatArray(3)

            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values)

            val adjustedRotationMatrix = getAdjustedRotationMatrix(rotationMatrix)

            val azimuthRadians = SensorManager.getOrientation(adjustedRotationMatrix, orientation)[0].toDouble()
            val pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()

            azimuth = lowPassDegreesFilter(azimuthRadians.toFloat())

            val orientationData = OrientationData(azimuth, pitch)
            orientationPublishSubject?.onNext(orientationData)
        }
    }

    private fun lowPassDegreesFilter(azimuthRadians: Float): Float {
        lastSin = alpha * lastSin + (1 - alpha) * sin(azimuthRadians)
        lastCos = alpha * lastCos + (1 - alpha) * cos(azimuthRadians)

        return ((Math.toDegrees(atan2(lastSin, lastCos).toDouble()) + 360) % 360).toFloat()
    }

    private fun getAdjustedRotationMatrix(rotationMatrix: FloatArray): FloatArray {
        val worldAxisX: Int
        val worldAxisY: Int

        when (windowManager.defaultDisplay.rotation) {
            ROTATION_90 -> {
                worldAxisX = SensorManager.AXIS_Z
                worldAxisY = SensorManager.AXIS_MINUS_X
            }
            ROTATION_180 -> {
                worldAxisX = SensorManager.AXIS_MINUS_X
                worldAxisY = SensorManager.AXIS_MINUS_Z
            }
            ROTATION_270 -> {
                worldAxisX = SensorManager.AXIS_MINUS_Z
                worldAxisY = SensorManager.AXIS_X
            }
            ROTATION_0 -> {
                worldAxisX = SensorManager.AXIS_X
                worldAxisY = SensorManager.AXIS_Z
            }
            else -> {
                worldAxisX = SensorManager.AXIS_X
                worldAxisY = SensorManager.AXIS_Z
            }
        }
        val adjustedRotationMatrix = FloatArray(9)
        SensorManager.remapCoordinateSystem(
            rotationMatrix, worldAxisX,
            worldAxisY, adjustedRotationMatrix
        )

        return adjustedRotationMatrix
    }

    fun setLowPassFilterAlpha(lowPassFilterAlpha: Float) {
        alpha = lowPassFilterAlpha
        Log.d("ALPHA", lowPassFilterAlpha.toString())
    }
}