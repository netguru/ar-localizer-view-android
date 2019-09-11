package co.netguru.android.arlocalizeralternative.common

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.netguru.android.arlocalizeralternative.R
import co.netguru.android.arlocalizeralternative.common.base.BaseActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class PermissionManager(private val activity: BaseActivity) {

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
        private const val CAMERA_REQUEST_CODE = 10
    }

    fun locationPermissionsGranted(): Boolean {
        return fineLocationPermissionGranted() && coarseLocationPermissionGranted()
    }

    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_REQUEST_CODE
        )
    }

    fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    private fun fineLocationPermissionGranted(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun coarseLocationPermissionGranted(): Boolean {
        val coarseLocationPermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        return coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationPermissionRequestSuccess(requestCode: Int, grantResults: IntArray): PermissionResult {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return PermissionResult.GRANTED
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(activity.findViewById(android.R.id.content),R.string.location_permission_not_granted_info,Snackbar.LENGTH_SHORT)
                    .setAction(R.string.permission_recheck_question) { requestLocationPermissions() }
                    .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                    .show()
            } else {
                return PermissionResult.NOT_GRANTED_PERMAMENTLY
            }
        }
        return PermissionResult.NOT_GRANTED
    }

    fun isCameraPermissionRequestSuccess(requestCode: Int, grantResults: IntArray): PermissionResult {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return PermissionResult.GRANTED
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(activity.findViewById(android.R.id.content),R.string.camera_permission_not_granted_info,Snackbar.LENGTH_SHORT)
                    .setAction(R.string.permission_recheck_question) { requestCameraPermissions() }
                    .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                    .show()
            } else {
                return PermissionResult.NOT_GRANTED_PERMAMENTLY
            }
        }
        return PermissionResult.NOT_GRANTED
    }

    fun cameraPermissionsGranted() =
        ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

}