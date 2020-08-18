package com.lamhx.trackme.utilities

import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

object PermissionUtils {

    const val PERMISSION_REQUEST_CODE_MAIN_FRAGMENT_FINE_LOCATION = 1

    fun FragmentActivity.checkSelfPermissionCompat(permission: String) =
        ActivityCompat.checkSelfPermission(this, permission)

    fun FragmentActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

    fun FragmentActivity.requestPermissionsCompat(
        permissionsArray: Array<String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
    }
}