package com.lamhx.trackme.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lamhx.trackme.R
import com.lamhx.trackme.data.WorkoutHistory
import com.lamhx.trackme.databinding.ListItemWorkoutHistoryBinding
import com.lamhx.trackme.utilities.GoogleMapUtils.addMarkerPointerToMap
import com.lamhx.trackme.utilities.GoogleMapUtils.addMarkerStarToMap
import com.lamhx.trackme.utilities.px
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

/**
 * History workout adapter
 * Show workout history
 */
class HistoryWorkoutAdapter :
    ListAdapter<WorkoutHistory, RecyclerView.ViewHolder>(WorkoutDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListItemWorkoutHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val holder = WorkoutViewHolder(binding)
        binding.mapWorkout.onCreate(null)
        binding.mapWorkout.onResume()
        binding.mapWorkout.getMapAsync(holder)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val workout = getItem(position)
        (holder as WorkoutViewHolder).bind(workout)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        (holder as WorkoutViewHolder).unbind()
    }
    /**
     * Workout holder to define layout
     */
    class WorkoutViewHolder(
        private val binding: ListItemWorkoutHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root), OnMapReadyCallback {

        var googleMap: GoogleMap? = null
        fun bind(item: WorkoutHistory) {
            binding.apply {
                //Binding data
                binding.workoutHistory = item
                mapBinding()
                executePendingBindings()
            }
        }

        fun unbind() {
            if(googleMap != null) {
                googleMap!!.clear()
            }
        }

        fun mapBinding() {
            if(googleMap == null) return

            MapsInitializer.initialize(binding.root.context)
            //Setup style for polyline
            val listLocation = binding.workoutHistory!!.listLocation
            val polylineOptions = PolylineOptions().add()
            polylineOptions?.color(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.root.resources.getColor(R.color.color_polyline, null)
                } else {
                    binding.root.resources.getColor(R.color.color_polyline)
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
                    googleMap!!.addMarkerPointerToMap(binding.root.context, latLng)
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
                    googleMap!!.addMarkerStarToMap(binding.root.context, latLng)
                }
                googleMap!!.addPolyline(polylineOptions)
            }
            //Redirect map to overview activity on map
            googleMap!!.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    LatLngBounds(LatLng(minLat, minLng), LatLng(maxLat, maxLng)),
                    30.px
                )
            )
        }

        override fun onMapReady(map: GoogleMap?) {
            if(map == null) return

            MapsInitializer.initialize(binding.root.context)
            //Disable gestures to map workout history
            map.uiSettings.setAllGesturesEnabled(false)
            map.uiSettings.isCompassEnabled = false
            map.uiSettings.isMapToolbarEnabled = false
            map.uiSettings.isScrollGesturesEnabled = false
            map.uiSettings.isZoomControlsEnabled = false

            googleMap = map

            mapBinding()
        }

    }
}

/**
 * Check diff
 */
private class WorkoutDiffCallback : DiffUtil.ItemCallback<WorkoutHistory>() {

    override fun areItemsTheSame(oldItem: WorkoutHistory, newItem: WorkoutHistory): Boolean {
        return oldItem.workout.workoutId == newItem.workout.workoutId
    }

    override fun areContentsTheSame(oldItem: WorkoutHistory, newItem: WorkoutHistory): Boolean {
        return oldItem == newItem
    }
}