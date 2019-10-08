package com.netguru.android.arlocalizeralternative.feature.location.domain

import com.netguru.android.arlocalizeralternative.feature.location.data.LocationRepository
import javax.inject.Inject

class CurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    fun getCurrentLocation() = locationRepository.getCurrentLocation()
}
