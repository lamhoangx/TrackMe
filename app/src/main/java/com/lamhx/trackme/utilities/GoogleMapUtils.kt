package com.lamhx.trackme.utilities

import android.content.Context
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.lamhx.trackme.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.lamhx.trackme.data.WorkoutHistory
import com.lamhx.trackme.utilities.GoogleMapUtils.addMarkerPointerToMap
import com.lamhx.trackme.utilities.GoogleMapUtils.addMarkerStarToMap

object GoogleMapUtils {
    // Value zoom default for GoogleMap
    const val DEFAULT_ZOOM = 15

    /**
     * Add marker icon [ic_marker] to Map with [latLng] to [mMap]
     */
    fun GoogleMap.addMarkerPointerToMap(context: Context, latLng: LatLng) {
        addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        AppCompatResources.getDrawable(context, R.drawable.ic_marker)
                            ?.toBitmap()
                    )
                )
        )
    }

    /**
     * Add marker icon [ic_star] to Map with [latLng] to [mMap]
     */
    fun GoogleMap.addMarkerStarToMap(context: Context, latLng: LatLng) {
        addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        AppCompatResources.getDrawable(context, R.drawable.ic_star)
                            ?.toBitmap()
                    )
                )
                .anchor(0.5F, 0.5F)
        )
    }

    /**
     * Binding data workout to map (list coordinates on workout)
     */
    fun mapBindingWorkout(googleMap: GoogleMap, context: Context, workoutHistory: WorkoutHistory) {
        //Setup style for polyline
        val listLocation = workoutHistory.listLocation
        val polylineOptions = PolylineOptions().add()
        polylineOptions?.color(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.resources.getColor(R.color.color_polyline, null)
            } else {
                context.resources.getColor(R.color.color_polyline)
            }
        )

        if(listLocation.isEmpty()) {
            return
        }

        //Find bound rect workout on map
        var minLat: Double = 0.toDouble()
        var minLng: Double = 0.toDouble()
        var maxLat: Double = 0.toDouble()
        var maxLng: Double = 0.toDouble()

        for (i in listLocation.indices) {
            val coor = listLocation[i]
            val latLng = LatLng(coor.latitude, coor.longitude)

            polylineOptions?.add(latLng)
            if (i == 0) {
                //start coor
                googleMap.addMarkerPointerToMap(context, latLng)
                minLat = coor.latitude
                maxLat = coor.latitude
                minLng = coor.longitude
                maxLng = coor.longitude

            } else {
                if (minLat > coor.latitude) {
                    minLat = coor.latitude
                }
                if (minLng > coor.longitude) {
                    minLng = coor.longitude
                }
                if (maxLat < coor.latitude) {
                    maxLat = coor.latitude
                }
                if (maxLng < coor.longitude) {
                    maxLng = coor.longitude
                }
            }
            if (i == listLocation.lastIndex) {
                //finish coor
                googleMap.addMarkerStarToMap(context, latLng)
            }
            googleMap.addPolyline(polylineOptions)
        }
        //Redirect map to overview activity on map
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                LatLngBounds(LatLng(minLat, minLng), LatLng(maxLat, maxLng)),
                30.px
            )
        )
    }
}

