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
        private const val ESSENTIAL_PERMISSIONS_REQUEST_CODE = 123
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA), ESSENTIAL_PERMISSIONS_REQUEST_CODE
        )
    }

    fun permissionsGranted(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        val cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED
                && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
                && cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    fun isPermissionsRequestSuccess(requestCode: Int, grantResults: IntArray): PermissionResult {
        if (requestCode == ESSENTIAL_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.find { it == PackageManager.PERMISSION_DENIED  } == null) {
                return PermissionResult.GRANTED
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                Snackbar.make(activity.findViewById(android.R.id.content),R.string.essential_permissions_not_granted_info,Snackbar.LENGTH_SHORT)
                    .setAction(R.string.permission_recheck_question) { requestPermissions() }
                    .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                    .show()
            } else {
                return PermissionResult.NOT_GRANTED_PERMAMENTLY
            }
        }
        return PermissionResult.NOT_GRANTED
    }
}