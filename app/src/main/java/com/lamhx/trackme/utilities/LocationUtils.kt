package com.lamhx.trackme.utilities

import android.app.Activity
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity

object LocationUtils {
    /**
     * Check GPS is ON or OFF
     */
    fun isGPSEnable(activity: Activity): Boolean {
        val locationManager =
            activity.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * @return Distance from list location by meter unit
     * @param [arrLocation] list location
     */
    fun getDistance(arrLocation: Array<Location>): Float {
        if (arrLocation.size <= 1) return 0F
        var distance: Float = 0F
        for (index in 1 until arrLocation.size) {
            distance += arrLocation[index].distanceTo(arrLocation[index - 1])
        }
        return distance
    }

    /**
     * Convert distance from meter to kilometer
     */
    fun convertDistanceToKm(distance: Float): Float {
        return distance / 1000F
    }

    /**
     * Convert duration from seconds to hours
     */
    fun convertDurationToHours(duration: Long): Float {
        return duration / 3600F
    }

    /**
     * Calculate speed (km/h)
     * @param  [distance] meter, [duration] seconds
     * @return speed (km/h)
     */
    fun calSpeed(distance: Float, duration: Long): Float {
        return convertDistanceToKm(distance) / convertDurationToHours(duration)
    }

    fun compareCoordinates(location0: Location, location1: Location): Boolean {
        return location0.latitude == location1.latitude
                && location0.longitude == location1.longitude
    }
}