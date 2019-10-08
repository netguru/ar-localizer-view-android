package com.netguru.android.arlocalizeralternative.feature.location.data

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.netguru.android.arlocalizeralternative.feature.location.domain.ILocationRepository
import com.netguru.arlocalizerview.location.LocationData
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationApi: LocationApi,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ILocationRepository {

    companion object {
        private const val OVERPASS_QUERY =
            "[out:json][timeout:25];(node[\"amenity\"=\"%s\"](%f,%f,%f,%f););out;>;"
        private const val LOCATION_ERROR = "Failed to get current location"
    }

    override fun getCurrentLocation(): Single<LatLng> {
        return Single.create { emitter ->
            fusedLocationProviderClient
                .lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result?.let { location ->
                        emitter.onSuccess(LatLng(location.latitude, location.longitude))
                    }
                } else {
                    emitter.onError(Throwable(LOCATION_ERROR))
                }
            }
        }
    }

    override fun getDestinations(
        amenity: String, southWestLatitude: Double,
        southWestLongitude: Double,
        northeastLatitude: Double,
        northeastLongitude: Double
    ): Single<List<LocationData>> {

        val data =
            String.format(
                Locale.US,
                OVERPASS_QUERY, amenity, southWestLatitude, southWestLongitude,
                northeastLatitude, northeastLongitude
            )

        return locationApi.nodes(data)
            .flatMap {
                Single.just(
                    it.elements
                        .map { locationNode ->
                            LocationData(locationNode.latitude, locationNode.longitude)
                        })
            }
    }
}
