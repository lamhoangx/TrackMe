package com.lamhx.trackme.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lamhx.trackme.data.WorkoutHistory
import com.lamhx.trackme.data.WorkoutRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for [MainFragment]
 */
class MainViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {
    val listWorkout = workoutRepository.workoutHistory
    fun onResume() {
    }

    fun deleteWorkoutHistory(workoutHistory: WorkoutHistory) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(workoutHistory)
        }
    }
}