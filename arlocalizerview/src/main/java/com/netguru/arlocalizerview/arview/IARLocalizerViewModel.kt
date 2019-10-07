package com.netguru.arlocalizerview.arview

import android.os.Bundle
import androidx.lifecycle.LiveData
import com.netguru.arlocalizerview.PermissionResult
import com.netguru.arlocalizerview.common.ViewState
import com.netguru.arlocalizerview.compass.CompassData
import com.netguru.arlocalizerview.location.LocationData


internal interface IARLocalizerViewModel {
    val permissionState: LiveData<PermissionResult>
    fun compassState(): LiveData<ViewState<CompassData>>
    fun setDestinations(destinations: List<LocationData>)
    fun setLowPassFilterAlpha(lowPassFilterAlpha: Float)
    fun onSaveInstanceState(bundle: Bundle)
    fun onRestoreInstanceState(bundle: Bundle)
    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )

    fun checkPermissions()
}
