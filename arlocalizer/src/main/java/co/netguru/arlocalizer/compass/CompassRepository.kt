package co.netguru.arlocalizer.compass

import co.netguru.arlocalizer.location.LocationData
import co.netguru.arlocalizer.location.LocationProvider
import co.netguru.arlocalizer.orientation.OrientationProvider
import io.reactivex.Flowable
import io.reactivex.rxkotlin.combineLatest
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

    var destinationsLocation: List<LocationData> = listOf()

    //TODO rework of the compassUpdates
    fun getCompassUpdates(): Flowable<CompassData> {
        orientationProvider.startSensorObservation()
        return locationProvider
            .getLocationUpdates()
            .combineLatest(orientationProvider.getSensorUpdates())
            .flatMap { (currentLocation, currentOrientation) ->
                val destinations = destinationsLocation
                    .map {
                        handleDestination(
                            currentLocation,
                            it,
                            currentOrientation.currentAzimuth
                        )
                    }
                val compassData = CompassData(
                    currentOrientation,
                    destinations,
                    getMaxDistance(destinations),
                    getMinDistance(destinations),
                    currentLocation
                )
                Flowable.just(compassData)
            }
            .doOnCancel { orientationProvider.stopSensorObservation() }
            .doOnTerminate { orientationProvider.stopSensorObservation() }
    }

    private fun getMaxDistance(destinations: List<DestinationData>) =
        destinations.maxBy { it.distanceToDestination }
            ?.distanceToDestination ?: 0

    private fun getMinDistance(destinations: List<DestinationData>) =
        destinations.minBy { it.distanceToDestination }
            ?.distanceToDestination ?: 0

    private fun handleDestination(
        currentLocation: LocationData,
        destinationLocation: LocationData,
        currentAzimuth: Float
    ): DestinationData {
        val headingAngle = calculateHeadingAngle(currentLocation, destinationLocation)

        val currentDestinationAzimuth =
            (headingAngle - currentAzimuth + MAXIMUM_ANGLE) % MAXIMUM_ANGLE

        val distanceToDestination = locationProvider.getDistanceBetweenPoints(
            currentLocation,
            destinationLocation
        )

        return DestinationData(
            currentDestinationAzimuth,
            distanceToDestination,
            destinationLocation
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

    fun setLowPassFilterAlpha(lowPassFilterAlpha: Float) {
        orientationProvider.setLowPassFilterAlpha(lowPassFilterAlpha)
    }
}
