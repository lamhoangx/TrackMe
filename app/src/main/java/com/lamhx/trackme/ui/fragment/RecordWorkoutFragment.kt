package com.lamhx.trackme.ui.fragment

import android.annotation.SuppressLint
import android.content.*
import android.location.Location
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lamhx.trackme.R
import com.lamhx.trackme.databinding.RecordWorkoutFragmentBinding
import com.lamhx.trackme.services.ForegroundOnlyLocationService
import com.lamhx.trackme.utilities.GoogleMapUtils.DEFAULT_ZOOM
import com.lamhx.trackme.utilities.GoogleMapUtils.addMarkerPointerToMap
import com.lamhx.trackme.utilities.InjectorUtils
import com.lamhx.trackme.utilities.LocationUtils
import com.lamhx.trackme.utilities.TimeUtils
import com.lamhx.trackme.ui.viewmodels.Contracts
import com.lamhx.trackme.ui.viewmodels.RecordWorkoutViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.lamhx.trackme.ui.activity.RecordWorkoutActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * RecordWorkout Fragment
 * Show into UI process workout with info details
 * + Workout live location
 * + Distance
 * + Speed
 * + Duration
 */
class RecordWorkoutFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: RecordWorkoutFragmentBinding
    private lateinit var inLeftAnim: Animation
    private lateinit var outLeftAnim: Animation
    private lateinit var inRightAnim: Animation
    private lateinit var outRightAnim: Animation

    private lateinit var mMap: GoogleMap

    private val viewModel: RecordWorkoutViewModel by viewModels {
        InjectorUtils.provideTrackMeSessionViewModelFactory()
    }

    //ForegroundService
    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null

    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver
    private var foregroundOnlyLocationServiceBound = false
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecordWorkoutFragmentBinding.inflate(inflater, container, false)
        inLeftAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
        outLeftAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_left)
        inRightAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
        outRightAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_right)

        //Click listener
        binding.btnPause.setOnClickListener { viewModel.updateState(Contracts.pause) }
        binding.btnResume.setOnClickListener { viewModel.updateState(Contracts.running) }
        binding.btnStop.setOnClickListener { viewModel.updateState(Contracts.stop) }

        //Setup ViewSwitcher
        binding.controller.visibility = View.INVISIBLE
        binding.controller.inAnimation = inLeftAnim
        binding.controller.outAnimation = outRightAnim

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        (childFragmentManager.findFragmentById(R.id.map_workout) as SupportMapFragment)
            .getMapAsync(this)

        //handle foregroundservice
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

        subscribeWorkout()

        return binding.root
    }

    /**
     * Update UI when data changed via [LiveData]
     */
    private fun subscribeWorkout() {
        //Workout state
        viewModel.currentWorkoutState.observe(viewLifecycleOwner) { result ->
            when (result) {
                Contracts.start -> {
                    //default
                }
                Contracts.running -> {

                    workoutOnResume()
                }
                Contracts.pause -> {
                    workoutOnPause()
                }
                Contracts.stop -> {
                    workoutOnStop()
                }
            }
        }

        //Manual update info to TextView
        viewModel.duration.observe(viewLifecycleOwner) {
            binding.tvDuration.text = TimeUtils.standardizeDuration(it)
        }
        viewModel.distance.observe(viewLifecycleOwner) {
            binding.tvDistance.text = String.format(
                getString(R.string.info_distance),
                LocationUtils.convertDistanceToKm(it)
            )
        }
        viewModel.speed.observe(viewLifecycleOwner) {
            binding.tvSpeed.text = String.format(getString(R.string.info_speed), it)
        }
        viewModel.infoNotice.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onLocationReady() {
        binding.controller.visibility = View.VISIBLE
        binding.workoutInfo.visibility = View.INVISIBLE
        //Start workout if necessary
        workoutOnStart()
    }

    // Region Fragment lifecycle
    override fun onStart() {
        super.onStart()
        //Start Location foreground service
        val serviceIntent = Intent(requireContext(), ForegroundOnlyLocationService::class.java)
        requireActivity().bindService(
            serviceIntent,
            foregroundOnlyServiceConnection,
            Context.BIND_AUTO_CREATE
        )
        //start listen location from foreground service
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
        createPolylineIfNecessary()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        polylineOptions = null
        if (foregroundOnlyLocationServiceBound) {
            requireActivity().unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        super.onStop()
    }

    // End region Fragment lifecycle

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        //setup Polyline
        mMap.isMyLocationEnabled = true
        createPolylineIfNecessary()
    }

    private fun createPolylineIfNecessary() {
        polylineOptions ?: PolylineOptions().add().also {
            val polylineColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resources.getColor(R.color.color_polyline, null)
            } else {
                resources.getColor(R.color.color_polyline)
            }
            it.color(polylineColor)
            polylineOptions = it
        }
    }

    ////////////////////// Region handle workout //////////////////////////////
    var polylineOptions: PolylineOptions? = null
    var currentLatLng: LatLng? = null

    /**
     * Receiver for location broadcasts from [ForegroundOnlyLocationService].
     */
    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            synchronized(this) {
                if (currentLatLng == null) {
                    //Start workout session
                    onLocationReady()
                }
                //restore
                val restore =
                    intent.hasExtra(ForegroundOnlyLocationService.EXTRA_RESTORE_LIST_LOCATION)
                handleWorkoutInfo(intent, restore)
            }
        }
    }

    // Monitors connection to the while-in-use service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationService?.notifyInfo()
            foregroundOnlyLocationService?.registryWorkout()
            //If foregroundService had running, just fire resume status to continue
            foregroundOnlyLocationServiceBound = true
            if (foregroundOnlyLocationService?.isStartedWorkout()!!) {
                if (foregroundOnlyLocationService?.isPauseWorkout()!!) {
                    workoutOnPause()
                } else {
                    workoutOnResume()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    ///////// Region Workout lifecycle ///////////////

    private fun workoutOnStart() {
        if (!foregroundOnlyLocationService?.isStartedWorkout()!!) {
            foregroundOnlyLocationService?.startWorkout()
        }
    }

    /**
     * Update pause state info to UI
     */
    private fun workoutOnPause() {
        foregroundOnlyLocationService?.pauseWorkout()
        if (binding.controller.currentView != binding.onPauseController) {
            binding.controller.inAnimation = inLeftAnim
            binding.controller.outAnimation = outRightAnim
            binding.controller.showNext()
        }
    }

    /**
     * Handle resume workout
     */
    private fun workoutOnResume() {
        foregroundOnlyLocationService?.resumeWorkout()
        if (binding.controller.currentView != binding.btnPause) {
            binding.controller.inAnimation = inRightAnim
            binding.controller.outAnimation = outLeftAnim
            binding.controller.showPrevious()
        }
    }

    /**
     * Stop and finish workout
     */
    private fun workoutOnStop() {
        //stop foreground service
        foregroundOnlyLocationService?.stopWorkout()
        val serviceIntent = Intent(requireContext(), ForegroundOnlyLocationService::class.java)
        requireActivity().stopService(serviceIntent)
        (requireActivity() as RecordWorkoutActivity).finishWorkout()
    }

    //////////////// End region Workout lifecycle ///////////////


    /**
     * Handle locations on workout and draw on [mMap]
     */
    @Synchronized
    private fun handleWorkoutInfo(intent: Intent, restoreData: Boolean) {
        //Distance
        val distance = intent.getFloatExtra(ForegroundOnlyLocationService.EXTRA_DISTANCE, 0F)
        //Duration
        val duration = intent.getLongExtra(ForegroundOnlyLocationService.EXTRA_DURATION, 0)

        if (restoreData) {
            val listLocation =
                intent.getParcelableArrayListExtra<Location>(ForegroundOnlyLocationService.EXTRA_RESTORE_LIST_LOCATION)
            if (listLocation != null) {
                //Resume state -> just restore data
                viewModel.prepareForRestoreWorkout()
                for (i in 0 until listLocation.size) {
                    val location = listLocation[i]
                    val latlng = LatLng(location.latitude, location.longitude)
                    if (i == 0) {
                        //Point to start
                        mMap.addMarkerPointerToMap(requireContext(), latlng)
                    }
                    onReceiveLatLng(latlng)
                    if (i == listLocation.size - 1) {
                        //last location
                        if (currentLatLng != null) {
                            //Keep current map zoom value
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                        } else {
                            //Using [DEFAULT_ZOOM] for map zoom value
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    latlng,
                                    DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                        //Update newest location
                        currentLatLng = latlng
                    }
                }
                mMap.addPolyline(polylineOptions)
            }
            uiScope.launch {
                viewModel.updateWorkoutInfo(distance, duration)
            }
            return
        }

        //Handle real-time info
        val location = intent.getParcelableExtra<Location>(
            ForegroundOnlyLocationService.EXTRA_LOCATION
        )

        if (location != null) {
            val latlng = LatLng(location.latitude, location.longitude)
            onReceiveLatLng(latlng)
            if (currentLatLng != null) {
                //Keep current map zoom value
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
            } else {
                //Using [DEFAULT_ZOOM] for map zoom value
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, DEFAULT_ZOOM.toFloat()))
                mMap.addMarkerPointerToMap(requireContext(), latlng)
            }
            //Update newest location
            currentLatLng = latlng

            mMap.addPolyline(polylineOptions)

            if (location.hasSpeed()) {
                val speed = (location.speed * 3600 / 1000)
                uiScope.launch {
                    viewModel.updateWorkoutCurrentSpeed(speed)
                }
                resetTriggerUpdateSpeed()
            }
        }

        uiScope.launch {
            viewModel.updateWorkoutInfo(distance, duration)
        }
    }

    /**
     * Handle [LatLng] had been received
     * Save to [viewModel] and update to [Polyline]
     */
    private fun onReceiveLatLng(latLng: LatLng) {
        polylineOptions?.add(latLng)
        viewModel.addLatLng(latLng)
    }

    /**
     * Reset trigger event to recheck current speed
     */
    private fun resetTriggerUpdateSpeed() {
        if (handler.hasMessages(MSG_UPDATE_SPEED)) {
            handler.removeMessages(MSG_UPDATE_SPEED)
        }
        handler.sendEmptyMessageDelayed(MSG_UPDATE_SPEED, TIME_DELAY_TO_CHECK_SPEED)
    }

    /**
     * Handle
     */
    private val handler: Handler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            MSG_UPDATE_SPEED -> {
                viewModel.updateWorkoutCurrentSpeed(0.0F)
            }
            else -> {
            }
        }
        return@Handler true
    }

    companion object {
        private const val MSG_UPDATE_SPEED: Int = 1

        //Delay time to recheck current speed
        private const val TIME_DELAY_TO_CHECK_SPEED = 30000L //30s
    }
}