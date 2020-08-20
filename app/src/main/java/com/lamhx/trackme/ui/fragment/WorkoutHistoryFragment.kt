package com.lamhx.trackme.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import com.lamhx.trackme.R
import com.lamhx.trackme.databinding.RecordWorkoutFragmentBinding
import com.lamhx.trackme.ui.viewmodels.WorkoutHistoryViewModel
import com.lamhx.trackme.utilities.*

class WorkoutHistoryFragment : Fragment(), OnMapReadyCallback {

    private var viewModel: WorkoutHistoryViewModel? = null
    private lateinit var binding: RecordWorkoutFragmentBinding
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RecordWorkoutFragmentBinding.inflate(inflater, container, false)
        binding.workoutController.visibility = View.GONE

        requireActivity().intent?.let { bundle ->
            val workoutId = bundle.getLongExtra(getString(R.string.argWorkoutId), -1L)
            if(workoutId != -1L) {
                viewModel = InjectorUtils.provideWorkoutHistoryViewModel(workoutId)

                viewModel!!.workoutHistory.observe(viewLifecycleOwner) { workoutHistory ->
                    workoutHistory?.let {workout ->
                        duration(binding.tvDuration, workout.workout.duration)
                        distance(binding.tvDistance, workout.workout.distance)
                        avgSpeed(binding.tvSpeed, workout.workout.distance, workout.workout.duration)

                        googleMap?.let {googleMap ->
                            GoogleMapUtils.mapBindingWorkout(googleMap, binding.root.context, workout)
                        }
                    }
                }
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        (childFragmentManager.findFragmentById(R.id.map_workout) as SupportMapFragment)
            .getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().intent?.let {
           val workoutId = it.getLongExtra(getString(R.string.argWorkoutId), -1L)
            if(workoutId != -1L) {
                return
            }
        }
        //Handle error
        Toast.makeText(context, getString(R.string.noti_error_workout), Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }

    override fun onMapReady(map: GoogleMap?) {
        if (map == null) return
        MapsInitializer.initialize(binding.root.context)
        googleMap = map

        viewModel?.workoutHistory?.value?.let {
            GoogleMapUtils.mapBindingWorkout(googleMap!!, binding.root.context, it)
        }
    }
}