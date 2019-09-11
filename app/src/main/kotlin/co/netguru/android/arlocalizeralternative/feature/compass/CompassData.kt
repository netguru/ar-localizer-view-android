package co.netguru.android.arlocalizeralternative.feature.compass

import co.netguru.android.arlocalizeralternative.feature.location.LocationData
import co.netguru.android.arlocalizeralternative.feature.orientation.OrientationData

class CompassData {
    var orientationData = OrientationData(0f,0f)
    var currentDestinationAzimuth = -1f
    var lastDestinationAzimuth = 0f

    var currentLocation: LocationData? = null
    var destinationLocation: LocationData? = null
    var distancetoDestination = 0
}