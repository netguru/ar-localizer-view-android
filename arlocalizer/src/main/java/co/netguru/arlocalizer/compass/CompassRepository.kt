package co.netguru.arlocalizer.compass

import androidx.lifecycle.MutableLiveData
import co.netguru.arlocalizer.common.Result
import co.netguru.arlocalizer.location.LocationData
import co.netguru.arlocalizer.location.LocationProvider
import co.netguru.arlocalizer.orientation.OrientationProvider
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.combineLatest
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

internal class CompassRepository @Inject constructor(
    private val orientationProvider: OrientationProvider,
    private val locationProvider: LocationProvider
) {

    companion object {
        private const val MAXIMUM_ANGLE = 360
    }

    private var compassDisposable: Disposable? = null
    var destinations: List<LocationData> = listOf()
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
                    destinations.forEachIndexed { index, locationData ->
                        handleDestination(
                            compassData,
                            currentLocation,
                            locationData,
                            index,
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
        index: Int,
        currentAzimuth: Float
    ) {
        val headingAngle = calculateHeadingAngle(currentLocation, destinationLocation)

        val currentDestinationAzimuth =
            (headingAngle - currentAzimuth + MAXIMUM_ANGLE) % MAXIMUM_ANGLE

        val distanceToDestination = locationProvider.getDistanceBetweenPoints(
            currentLocation,
            destinationLocation
        )
        compassData.destinations.add(
            index, DestinationData(
                currentDestinationAzimuth,
                distanceToDestination,
                destinationLocation
            )
        )
    }

    private fun calculateHeadingAngle(currentLocation: LocationData, destinationLocation: LocationData): Float {
        val currentLatitudeRadians = Math.toRadians(currentLocation.latitude)
        val destinationLatitudeRadians = Math.toRadians(destinationLocation.latitude)
        val deltaLongitude = Math.toRadians(destinationLocation.longitude - currentLocation.longitude)

        val y = cos(currentLatitudeRadians) * sin(destinationLatitudeRadians) -
                sin(currentLatitudeRadians) * cos(destinationLatitudeRadians) * cos(deltaLongitude)
        val x = sin(deltaLongitude) * cos(destinationLatitudeRadians)
        val headingAngle = Math.toDegrees(atan2(x, y)).toFloat()

        return (headingAngle + MAXIMUM_ANGLE) % MAXIMUM_ANGLE
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
