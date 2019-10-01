package co.netguru.arlocalizer.arview

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.toLiveData
import co.netguru.arlocalizer.PermissionManager
import co.netguru.arlocalizer.PermissionResult
import co.netguru.arlocalizer.common.ViewState
import co.netguru.arlocalizer.compass.CompassData
import co.netguru.arlocalizer.compass.CompassRepository
import co.netguru.arlocalizer.location.LocationData
import javax.inject.Inject

internal class ARLocalizerViewModel @Inject constructor(
    private val compassRepository: CompassRepository,
    private val permissionManager: PermissionManager
) : IARLocalizerViewModel {

    companion object {
        private const val LOCATION_DATA = "location_data"
        private const val UNEXPECTED_ERROR_MESSAGE = "Unexpected error"
    }

    override val permissionState: MutableLiveData<PermissionResult> = MutableLiveData()

    override fun setDestinations(destinations: List<LocationData>) {
        if (!permissionManager.areAllPermissionsGranted()) checkPermissions()
        compassRepository.destinationsLocation = destinations
    }

    override fun compassState(): LiveData<ViewState<CompassData>> {
        return compassRepository.getCompassUpdates()
            .map<ViewState<CompassData>> { ViewState.Success(it) }
            .onErrorReturn { ViewState.Error(it.localizedMessage ?: UNEXPECTED_ERROR_MESSAGE) }
            .toLiveData()
    }

    override fun setLowPassFilterAlpha(lowPassFilterAlpha: Float) {
        compassRepository.setLowPassFilterAlpha(lowPassFilterAlpha)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        compassRepository.destinationsLocation.let {
            bundle.putParcelableArrayList(LOCATION_DATA, ArrayList(it))
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        (bundle.get(LOCATION_DATA) as? ArrayList<LocationData>)?.let {
            compassRepository.destinationsLocation = it
        }
    }

    override fun checkPermissions() {
        if (permissionManager.areAllPermissionsGranted()) {
            permissionState.postValue(
                PermissionResult.GRANTED
            )
        } else permissionManager.requestAllPermissions()
    }

    override fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionResult =
            permissionManager.getPermissionsRequestResult(requestCode, grantResults)
        permissionState.postValue(
            permissionResult
        )
    }
}
