package co.netguru.arlocalizer.compass

import co.netguru.arlocalizer.location.LocationData
import co.netguru.arlocalizer.orientation.OrientationData

internal class CompassData {
    var orientationData = OrientationData(0f, 0f)
    val destinations = arrayListOf<DestinationData>()
    var currentLocation: LocationData? = null
}
