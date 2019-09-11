package co.netguru.android.arlocalizeralternative.feature.compass

import android.util.Log
import androidx.lifecycle.MutableLiveData
import co.netguru.android.arlocalizeralternative.feature.location.LocationData
import co.netguru.android.arlocalizeralternative.feature.location.LocationProvider
import co.netguru.android.arlocalizeralternative.feature.orientation.OrientationProvider
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import co.netguru.android.arlocalizeralternative.common.Result

class CompassRepository @Inject constructor(
    private val orientationProvider: OrientationProvider,
    private val locationProvider: LocationProvider
) {

    private var locationDisposable: Disposable? = null
    private var orientationDisposable: Disposable? = null
    private val compassData = CompassData()
    var destination: LocationData? = null
        set(value) {
            field = value
            compassData.destinationLocation = value
        }
    val compassStateLiveData = MutableLiveData<Result<CompassData>>()

    fun startSensorObservation() {
        orientationProvider.startSensorObservation()
        orientationDisposable = orientationProvider.getSensorUpdates()
            ?.subscribe({ orientationData ->
                compassData.orientationData = orientationData
                destination?.let {
                    handleDestination(compassData.currentLocation, it)
                }
                compassStateLiveData.value = Result.Success(compassData)
            }, { throwable -> compassStateLiveData.value = Result.Error(throwable) })
    }

    fun startLocationObservation() {
        locationDisposable = locationProvider.getLocationUpdates()
            .subscribe({ location ->
                compassData.currentLocation = location
                compassStateLiveData.value = Result.Success(compassData)
            }, { throwable -> compassStateLiveData.value = Result.Error(throwable) })
    }

    private fun handleDestination(currentLocation: LocationData?, destinationLocation: LocationData) {
        if (currentLocation == null) return
        compassData.lastDestinationAzimuth = compassData.currentDestinationAzimuth
        val headingAngle = calculateHeadingAngle(currentLocation, destinationLocation)

        compassData.currentDestinationAzimuth = (headingAngle - compassData.orientationData.currentAzimuth + 360) % 360

        compassData.distancetoDestination = locationProvider.getDistanceBetweenPoints(compassData.currentLocation,
            compassData.destinationLocation)
    }

    private fun calculateHeadingAngle(currentLocation: LocationData, destinationLocation: LocationData): Float {
        val currentLatitudeRadians = Math.toRadians(currentLocation.latitude)
        val destinationLatitudeRadians = Math.toRadians(destinationLocation.latitude)
        val deltaLongitude = Math.toRadians(destinationLocation.longitude - currentLocation.longitude)

        val y = cos(currentLatitudeRadians) * sin(destinationLatitudeRadians) - sin(currentLatitudeRadians) * cos(destinationLatitudeRadians) * cos(deltaLongitude)
        val x = sin(deltaLongitude) * cos(destinationLatitudeRadians)
        val headingAngle = Math.toDegrees(atan2(x, y)).toFloat()

        return (headingAngle + 360) % 360
    }

    fun stopSensorObservation() {
        orientationProvider.stopSensorObservation()
        orientationDisposable?.dispose()
    }

    fun stopLocationObservation() {
        locationDisposable?.dispose()
    }

    fun setLowPassFilterAlpha(lowPassFilterAlpha: Float) {
        orientationProvider.setLowPassFilterAlpha(lowPassFilterAlpha)
    }
}
