package com.lamhx.trackme.utilities

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.lamhx.trackme.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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
}