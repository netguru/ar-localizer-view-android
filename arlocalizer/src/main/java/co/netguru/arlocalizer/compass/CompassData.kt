package co.netguru.arlocalizer.compass

import co.netguru.arlocalizer.location.LocationData
import co.netguru.arlocalizer.orientation.OrientationData

internal data class CompassData(
    val orientationData: OrientationData,
    val destinations: List<DestinationData>,
    val maxDistance: Int,
    val minDistance: Int,
    val currentLocation: LocationData
)
