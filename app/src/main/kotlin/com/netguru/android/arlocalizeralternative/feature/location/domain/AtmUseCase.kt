package com.netguru.android.arlocalizeralternative.feature.location.domain

import com.google.android.gms.maps.model.LatLngBounds
import com.netguru.android.arlocalizeralternative.feature.location.data.LocationRepository
import javax.inject.Inject

class AtmUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    fun getATM(
        latLngBounds: LatLngBounds
    ) = locationRepository.getDestinations(
        ATM_AMENITY,
        latLngBounds.southwest.latitude,
        latLngBounds.southwest.longitude,
        latLngBounds.northeast.latitude,
        latLngBounds.northeast.longitude
    )

    companion object {
        private const val ATM_AMENITY = "atm"
    }
}
