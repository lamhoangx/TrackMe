package com.lamhx.trackme.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lamhx.trackme.data.WorkoutHistory
import com.lamhx.trackme.data.WorkoutRepository
import kotlinx.coroutines.launch

class WorkoutHistoryViewModel(
    private val workoutRepository: WorkoutRepository,
    private val workoutId: Long
) : ViewModel() {
    lateinit var workoutHistory: LiveData<WorkoutHistory?>
    init {
        fetchWorkoutHistory()
    }

    private fun fetchWorkoutHistory() {
        viewModelScope.launch {
            workoutHistory = workoutRepository.getWorkoutHistory(workoutId)
        }
    }
}