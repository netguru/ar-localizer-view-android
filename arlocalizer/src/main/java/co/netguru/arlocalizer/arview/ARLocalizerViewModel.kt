package co.netguru.arlocalizer.arview

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import co.netguru.arlocalizer.PermissionManager
import co.netguru.arlocalizer.PermissionResult
import co.netguru.arlocalizer.common.Result
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
    }

    override val compassState: LiveData<ViewState<CompassData>> =
        Transformations.map(compassRepository.compassStateLiveData) { compassDataResult: Result<CompassData> ->
            when (compassDataResult) {
                is Result.Success -> ViewState.Success(compassDataResult.data)
                is Result.Error -> {
                    stopCompass()
                    ViewState.Error<CompassData>(
                        compassDataResult.throwable.message ?: "Unexpected error"
                    )
                }
            }
        }

    override val permissionState: MutableLiveData<PermissionResult> = MutableLiveData()

    override fun setDestinations(destinations: List<LocationData>) {
        if (!permissionManager.areAllPermissionsGranted()) checkPermissions()
        compassRepository.destinations = destinations
    }

    override fun startCompass() {
        if (permissionManager.areAllPermissionsGranted()) compassRepository.startCompass()
    }

    override fun stopCompass() {
        compassRepository.stopCompass()
    }

    override fun setLowPassFilterAlpha(lowPassFilterAlpha: Float) {
        compassRepository.setLowPassFilterAlpha(lowPassFilterAlpha)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        compassRepository.destinations.let {
            bundle.putParcelableArrayList(LOCATION_DATA, ArrayList(it))
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        (bundle.get(LOCATION_DATA) as? ArrayList<LocationData>)?.let {
            compassRepository.destinations = it
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
        when (permissionResult) {
            PermissionResult.GRANTED ->
                if (!compassRepository.hasCompassStarted()) startCompass()
            else -> Unit
        }
        permissionState.postValue(
            permissionResult
        )
    }
}
