package co.netguru.arlocalizer.compass

import co.netguru.arlocalizer.location.LocationData
import co.netguru.arlocalizer.orientation.OrientationData

internal class CompassData {
    var orientationData = OrientationData(0f, 0f)
    var currentDestinationAzimuth = -1f

    var currentLocation: LocationData? = null
    var destinationLocation: LocationData? = null
    var distanceToDestination = 0
}