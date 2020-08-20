package com.lamhx.trackme.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.lamhx.trackme.R
import com.lamhx.trackme.adapters.HistoryWorkoutAdapter
import com.lamhx.trackme.adapters.OnWorkoutHistoryListener
import com.lamhx.trackme.data.WorkoutHistory
import com.lamhx.trackme.databinding.MainFragmentBinding
import com.lamhx.trackme.ui.activity.RecordWorkoutActivity
import com.lamhx.trackme.ui.activity.WorkoutHistoryActivity
import com.lamhx.trackme.utilities.InjectorUtils
import com.lamhx.trackme.utilities.PermissionUtils.checkSelfPermissionCompat
import com.lamhx.trackme.ui.viewmodels.MainViewModel

/**
 * Main fragment
 * Present workout history and button to start record new workout
 */
class MainFragment : Fragment(), OnWorkoutHistoryListener {

    private lateinit var binding: MainFragmentBinding

    //ViewModel for MainFragment
    private val viewModel: MainViewModel by viewModels {
        InjectorUtils.provideMainViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.btnRecord.setOnClickListener {
            navigateToTrackSession()
        }

        val adapter = HistoryWorkoutAdapter(this)
        binding.historyWorkout.adapter = adapter
        subscribeUi(adapter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onOpenWorkoutHistory(workoutHistory: WorkoutHistory?) {
        workoutHistory?.let {
            navigateToWorkoutHistoryFragment(workoutHistory.workout.workoutId)
        }
    }

    override fun onDeleteWorkoutHistory(workoutHistory: WorkoutHistory?) {
        workoutHistory?.let {
            viewModel.deleteWorkoutHistory(workoutHistory)
        }
    }

    /**
     * Listen workout history change to update UI
     */
    private fun subscribeUi(adapter: HistoryWorkoutAdapter) {
        viewModel.listWorkout.observe(viewLifecycleOwner) { workoutsHistory ->
            if (workoutsHistory.isEmpty()) {
                binding.emptyWorkoutNotice.visibility = View.VISIBLE
            } else {
                binding.emptyWorkoutNotice.visibility = View.GONE
            }
            binding.progressbarLoading.visibility = View.GONE
            adapter.submitList(workoutsHistory)
            //scroll to top
            Handler().postDelayed({
                binding.historyWorkout.smoothScrollToPosition(0);
            }, 500)

        }
    }

    /**
     * Navigate to record workout screen
     */
    private fun navigateToTrackSession() {
        if (alreadyLocationPermission()) {
            val startRecordWorkout = Intent(requireContext(), RecordWorkoutActivity::class.java)
            requireActivity().startActivity(startRecordWorkout)
        } else {
            Toast.makeText(
                context,
                getString(R.string.location_permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToWorkoutHistoryFragment(workoutId: Long) {
 /*       val directions = MainFragmentDirections.actionMainFragmentToWorkoutHistory(workoutId)
        findNavController().navigate(directions)*/
        val startWorkoutHistory = Intent(requireContext(), WorkoutHistoryActivity::class.java)
        startWorkoutHistory.putExtra(getString(R.string.argWorkoutId), workoutId)
        requireActivity().startActivity(startWorkoutHistory)
    }

    /**
     * Check list permission need to required
     */
    private fun alreadyLocationPermission(): Boolean {
        return (requireActivity().checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

}