package co.netguru.arlocalizer.location

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlin.math.roundToInt

/**
 * Created by Mateusz on 10.04.2019.
 */

internal class LocationProvider(private val rxLocation: RxLocation) {

    private val locationRequest = LocationRequest().apply {
        interval = 5000
        fastestInterval = 20
        smallestDisplacement = 1f
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @SuppressLint("MissingPermission")
    fun getLocationUpdates(): Flowable<LocationData> {
        return rxLocation.settings().checkAndHandleResolution(locationRequest)
            .toObservable()
            .flatMap { rxLocation.location().updates(locationRequest) }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .toFlowable(BackpressureStrategy.LATEST)
            .flatMap { location -> Flowable.just(
                LocationData(
                    location.latitude,
                    location.longitude
                )
            ) }
    }

    fun getDistanceBetweenPoints(currentLocation: LocationData?,
                                 destinationLocation: LocationData?): Int {
        val locationA = Location("A")

        locationA.latitude = currentLocation?.latitude ?: 0.0
        locationA.longitude = currentLocation?.longitude ?: 0.0

        val locationB = Location("B")

        locationB.latitude = destinationLocation?.latitude ?: 0.0
        locationB.longitude = destinationLocation?.longitude ?: 0.0

        return locationA.distanceTo(locationB).roundToInt()
    }
}