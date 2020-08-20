package com.lamhx.trackme.adapters

import android.os.Build
import android.util.Log
import android.view.*
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
import com.lamhx.trackme.utilities.GoogleMapUtils

/**
 * History workout adapter
 * Show workout history
 */
class HistoryWorkoutAdapter (private val onWorkoutHistoryListener: OnWorkoutHistoryListener) :
    ListAdapter<WorkoutHistory, RecyclerView.ViewHolder>(WorkoutDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListItemWorkoutHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val holder = WorkoutViewHolder(binding, onWorkoutHistoryListener)
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
        private val binding: ListItemWorkoutHistoryBinding,
        private val listener: OnWorkoutHistoryListener
    ) : RecyclerView.ViewHolder(binding.root), OnMapReadyCallback,
        View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        var googleMap: GoogleMap? = null
        init {
            binding.moreMenu.setOnCreateContextMenuListener(this)
            binding.moreMenu.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.moreMenu.showContextMenu(it.x, it.y)
                } else {
                    binding.moreMenu.showContextMenu()
                }
            }
            binding.root.setOnClickListener {
                listener.onOpenWorkoutHistory(binding.workoutHistory)
            }

        }
        fun bind(item: WorkoutHistory) {
            binding.apply {
                //Binding data
                binding.workoutHistory = item
                googleMap?.let {googleMap ->
                    binding.workoutHistory?.let { workoutHistory ->
                        GoogleMapUtils.mapBindingWorkout(googleMap, binding.root.context, workoutHistory)
                    }
                }
                executePendingBindings()
            }
        }

        fun unbind() {
            if(googleMap != null) {
                googleMap!!.clear()
            }
        }

        override fun onMapReady(map: GoogleMap?) {
            if (map == null) return

            MapsInitializer.initialize(binding.root.context)
            //Disable gestures to map workout history
            map.uiSettings.setAllGesturesEnabled(false)
            map.uiSettings.isCompassEnabled = false
            map.uiSettings.isMapToolbarEnabled = false
            map.uiSettings.isScrollGesturesEnabled = false
            map.uiSettings.isZoomControlsEnabled = false

            googleMap = map

            googleMap?.let {googleMap ->
                binding.workoutHistory?.let { workoutHistory ->
                    GoogleMapUtils.mapBindingWorkout(googleMap, binding.root.context, workoutHistory)
                }
            }
        }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            view: View?,
            contextMenuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu.add(Menu.NONE, R.id.menu_open, Menu.NONE, R.string.menu_open)
                .setOnMenuItemClickListener(this)
            menu.add(Menu.NONE, R.id.menu_delete, Menu.NONE, R.string.menu_delete)
                .setOnMenuItemClickListener(this)
        }

        override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
            menuItem?.let {
                when(it.itemId) {
                    R.id.menu_open -> {
                        listener.onOpenWorkoutHistory(binding.workoutHistory)
                    }
                    R.id.menu_delete -> {
                        listener.onDeleteWorkoutHistory(binding.workoutHistory)
                    }
                }
            }
            return true
        }
    }
}

interface OnWorkoutHistoryListener {
    fun onOpenWorkoutHistory(workoutHistory: WorkoutHistory?)
    fun onDeleteWorkoutHistory(workoutHistory: WorkoutHistory?)
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