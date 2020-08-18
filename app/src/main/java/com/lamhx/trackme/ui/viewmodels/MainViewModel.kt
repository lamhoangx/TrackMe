package com.lamhx.trackme.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.lamhx.trackme.data.WorkoutRepository

/**
 * ViewModel for [MainFragment]
 */
class MainViewModel(workoutRepository: WorkoutRepository) : ViewModel() {
    val listWorkout = workoutRepository.workoutHistory
    fun onResume() {
    }
}