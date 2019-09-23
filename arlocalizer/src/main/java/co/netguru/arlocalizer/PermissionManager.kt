package co.netguru.arlocalizer

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal class PermissionManager(private val activity: Activity) {

    companion object {
        private const val ESSENTIAL_PERMISSIONS_REQUEST_CODE = 123
    }

    fun requestAllPermissions() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
            ),
            ESSENTIAL_PERMISSIONS_REQUEST_CODE
        )
    }

    fun areAllPermissionsGranted(): Boolean {
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

    fun getPermissionsRequestResult(requestCode: Int, grantResults: IntArray): PermissionResult {
        if (requestCode != ESSENTIAL_PERMISSIONS_REQUEST_CODE) return PermissionResult.NOT_GRANTED

        if (grantResults.none { it == PackageManager.PERMISSION_DENIED }) {
            return PermissionResult.GRANTED
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            || ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            )
        ) {
            return PermissionResult.SHOW_RATIONALE
        }
        return PermissionResult.NOT_GRANTED
    }
}