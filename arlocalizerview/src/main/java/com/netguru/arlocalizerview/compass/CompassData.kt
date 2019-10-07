package com.netguru.arlocalizerview.compass

import com.netguru.arlocalizerview.location.LocationData
import com.netguru.arlocalizerview.orientation.OrientationData

internal data class CompassData(
    val orientationData: OrientationData,
    val destinations: List<DestinationData>,
    val maxDistance: Int,
    val minDistance: Int,
    val currentLocation: LocationData
)
