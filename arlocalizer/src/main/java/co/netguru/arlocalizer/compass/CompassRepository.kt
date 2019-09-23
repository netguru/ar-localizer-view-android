package co.netguru.arlocalizer.compass

import androidx.lifecycle.MutableLiveData
import co.netguru.arlocalizer.location.LocationData
import co.netguru.arlocalizer.location.LocationProvider
import co.netguru.arlocalizer.orientation.OrientationProvider
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import co.netguru.arlocalizer.common.Result
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.combineLatest
import io.reactivex.rxkotlin.subscribeBy

internal class CompassRepository @Inject constructor(
    private val orientationProvider: OrientationProvider,
    private val locationProvider: LocationProvider
) {

    private var compassDisposable: Disposable? = null
    var destination: LocationData? = null
    val compassStateLiveData = MutableLiveData<Result<CompassData>>()

    fun startCompass() {
        orientationProvider.startSensorObservation()
        compassDisposable = locationProvider
            .getLocationUpdates()
            .combineLatest(orientationProvider.getSensorUpdates())
            .subscribeBy(
                onNext = { (currentLocation, currentOrientation) ->
                    val compassData = CompassData().apply {
                        this.currentLocation = currentLocation
                        this.orientationData = currentOrientation
                    }
                    destination?.run {
                        handleDestination(
                            compassData,
                            currentLocation,
                            this,
                            currentOrientation.currentAzimuth
                        )
                    }
                    compassStateLiveData.postValue(Result.Success(compassData))
                }, onError = {
                    compassStateLiveData.postValue(Result.Error(it))
                }
            )
    }

    private fun handleDestination(
        compassData: CompassData,
        currentLocation: LocationData,
        destinationLocation: LocationData,
        currentAzimuth: Float
    ) {
        val headingAngle = calculateHeadingAngle(currentLocation, destinationLocation)

        val currentDestinationAzimuth = (headingAngle - currentAzimuth + 360) % 360

        val distanceToDestination = locationProvider.getDistanceBetweenPoints(
            currentLocation,
            destinationLocation)
        compassData.currentDestinationAzimuth = currentDestinationAzimuth
        compassData.distanceToDestination = distanceToDestination
        compassData.destinationLocation = destinationLocation
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

    fun stopCompass() {
        orientationProvider.stopSensorObservation()
        compassDisposable?.dispose()
    }

    fun hasCompassStarted() = compassDisposable != null && compassDisposable?.isDisposed != true

    fun setLowPassFilterAlpha(lowPassFilterAlpha: Float) {
        orientationProvider.setLowPassFilterAlpha(lowPassFilterAlpha)
    }
}
