package com.netguru.android.arlocalizeralternative.feature.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.netguru.arlocalizerview.location.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class LocationViewModel @Inject constructor(private val fusedLocationProviderClient: FusedLocationProviderClient) :
    ViewModel() {

    private val mutableLocation = MutableLiveData<LatLng>()
    val location: LiveData<LatLng> = mutableLocation
    private val mutableDestinations = MutableLiveData<List<LocationData>>()
    val destinations: LiveData<List<LocationData>> = mutableDestinations
    private val mutableViewMode = MutableLiveData<ViewMode>(ViewMode.MapMode)
    val viewMode: LiveData<ViewMode> = mutableViewMode

    fun onMapReady() {
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (hasCurrentLocation()) return
        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener {
                it.result?.let { location ->
                    mutableLocation.postValue(LatLng(location.latitude, location.longitude))
                }
            }
    }

    private fun hasCurrentLocation() = mutableLocation.value != null

    fun handleDestinations(destinations: List<LocationData>) {
        mutableDestinations.postValue(destinations)
    }

    fun arModeClick() {
        mutableViewMode.postValue(ViewMode.ARMode)
    }

    fun mapModeClick() {
        mutableViewMode.postValue(ViewMode.MapMode)
    }
}
