package co.netguru.arlocalizer.compass

import co.netguru.arlocalizer.location.LocationData

internal data class DestinationData
    (
    val currentDestinationAzimuth: Float,
    val distanceToDestination: Int,
    val destinationLocation: LocationData
)
