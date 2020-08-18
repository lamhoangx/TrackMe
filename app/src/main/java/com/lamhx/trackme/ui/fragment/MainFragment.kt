package com.lamhx.trackme.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.lamhx.trackme.R
import com.lamhx.trackme.adapters.HistoryWorkoutAdapter
import com.lamhx.trackme.databinding.MainFragmentBinding
import com.lamhx.trackme.ui.activity.RecordWorkoutActivity
import com.lamhx.trackme.utilities.InjectorUtils
import com.lamhx.trackme.utilities.PermissionUtils.checkSelfPermissionCompat
import com.lamhx.trackme.ui.viewmodels.MainViewModel

/**
 * Main fragment
 * Present workout history and button to start record new workout
 */
class MainFragment : Fragment() {

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

        val adapter = HistoryWorkoutAdapter()
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

    /**
     * Check list permission need to required
     */
    private fun alreadyLocationPermission(): Boolean {
        return (requireActivity().checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

}