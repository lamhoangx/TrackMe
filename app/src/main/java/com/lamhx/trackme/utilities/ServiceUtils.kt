package com.lamhx.trackme.utilities

import android.app.ActivityManager
import android.content.Context

/**
 * Service utils
 */
class ServiceUtils {
    /**
     * Check [serviceClass] is running or not
     */
    private fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}