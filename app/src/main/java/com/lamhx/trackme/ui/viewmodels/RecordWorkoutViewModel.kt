package com.lamhx.trackme.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lamhx.trackme.data.Coordinates
import com.lamhx.trackme.data.Workout
import com.lamhx.trackme.data.WorkoutRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Viewmodel for [RecordWorkoutFragment]
 */
class RecordWorkoutViewModel(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    @Contracts.WorkoutState
    private var workoutState = Contracts.running
    val currentWorkoutState = MutableLiveData<Int>()
    val infoNotice = MutableLiveData<String>()

    /**
     * Duration workout, unit is seconds
     */
    val duration = MutableLiveData<Long>(0L)

    /**
     * Distance workout, unit is meter
     */
    val distance = MutableLiveData<Float>(0F)

    /**
     * Speed workout, unit is km/h
     */
    val speed = MutableLiveData<Float>(0F)

    private val listLatLng = ArrayList<LatLng>()


    /**
     * Finish workout
     * Save data workout to database
     */
    private fun stopWorkout() {
        if (listLatLng.isEmpty()) {
            infoNotice.value = ERRROR_WORKOUT_NO_TRACK
            return
        }
        runBlocking {
            launch {
                val currentTimeSubmit = System.currentTimeMillis()
                val arrCoordinates = ArrayList<Coordinates>()
                for (latLng in listLatLng) {
                    arrCoordinates.add(
                        Coordinates(
                            currentTimeSubmit,
                            latLng.latitude,
                            latLng.longitude
                        )
                    )
                }
                workoutRepository.saveWorkout(
                    Workout(currentTimeSubmit, distance.value!!, duration.value!!),
                    arrCoordinates.toList()
                )
            }
        }
    }

    /**
     * Pause workout
     */
    fun pauseWorkout() {
    }

    /**
     * Resume workout
     */
    fun resumeWorkout() {
    }

    /**
     * Update workout's state
     * @param [state] current state
     */
    fun updateState(@Contracts.WorkoutState state: Int) {
        workoutState = state
        when (state) {
            Contracts.running -> {
                resumeWorkout()
            }
            Contracts.pause -> {
                pauseWorkout()
            }
            Contracts.stop -> {
                stopWorkout()
            }
        }
        currentWorkoutState.value = workoutState
    }

    /**
     * Add location had track to viewmodel
     */
    fun addLatLng(latLng: LatLng) {
        listLatLng.add(latLng)
    }

    /**
     * Update workout's distance to viewmodel
     * @param [distance] unit is meter
     */
    fun updateWorkoutDistanceInfo(distance: Float) {
        this.distance.value = distance
    }

    /**
     * Update workout's duration to viewmodel
     * @param [duration] unit is seconds
     */
    fun updateWorkoutDurationInfo(duration: Long) {
        this.duration.value = duration
    }

    /**
     * Update current workout's speed to viewmodel
     */
    fun updateWorkoutCurrentSpeed(speed: Float) {
        this.speed.value = speed
    }

    /**
     * Update workout's info to viewmodel
     * @param [duration] unit is seconds,  [distance] unit is meter
     */
    fun updateWorkoutInfo(distance: Float, duration: Long) {
        this.distance.value = distance
        this.duration.value = duration
    }

    fun prepareForRestoreWorkout() {
        listLatLng.clear()
    }

    companion object {
        const val ERRROR_WORKOUT_NO_TRACK = "Failed cuz without workout"
    }
}