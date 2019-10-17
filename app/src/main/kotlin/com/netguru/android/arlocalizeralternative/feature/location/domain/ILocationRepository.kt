package com.netguru.android.arlocalizeralternative.feature.location.domain

import com.google.android.gms.maps.model.LatLng
import com.netguru.arlocalizerview.location.LocationData
import io.reactivex.Single

interface ILocationRepository {
    fun getDestinations(
        amenity: String,
        southWestLatitude: Double,
        southWestLongitude: Double,
        northeastLatitude: Double,
        northeastLongitude: Double
    ): Single<List<LocationData>>

    fun getCurrentLocation(): Single<LatLng>
}
