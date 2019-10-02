package co.netguru.arlocalizer.arview

import android.os.Bundle
import androidx.lifecycle.LiveData
import co.netguru.arlocalizer.PermissionResult
import co.netguru.arlocalizer.common.ViewState
import co.netguru.arlocalizer.compass.CompassData
import co.netguru.arlocalizer.location.LocationData


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
