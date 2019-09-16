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
                Manifest.permission.CAMERA
            ), ESSENTIAL_PERMISSIONS_REQUEST_CODE
        )
    }

    fun permissionsGranted(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                && hasPermission(Manifest.permission.CAMERA)
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isPermissionsRequestSuccess(requestCode: Int, grantResults: IntArray): PermissionResult {
        if (requestCode != ESSENTIAL_PERMISSIONS_REQUEST_CODE) return PermissionResult.NOT_GRANTED

        if (grantResults.none { it == PackageManager.PERMISSION_DENIED }) {
            return PermissionResult.GRANTED
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
            || ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA)) {
            Snackbar.make(
                activity.findViewById(android.R.id.content),
                R.string.essential_permissions_not_granted_info,
                Snackbar.LENGTH_SHORT)
                .setAction(R.string.permission_recheck_question) { requestPermissions() }
                .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                .show()
            return PermissionResult.NOT_GRANTED
        } else {
            return PermissionResult.NOT_GRANTED_PERMAMENTLY
        }
    }
}