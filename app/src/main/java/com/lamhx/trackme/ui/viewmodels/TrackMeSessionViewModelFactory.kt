package com.lamhx.trackme.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lamhx.trackme.data.WorkoutRepository

/**
 * Factory for creating a [RecordWorkoutViewModel]
 */
class TrackMeSessionViewModelFactory (
    private val workoutRepository: WorkoutRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordWorkoutViewModel(workoutRepository) as T
    }
}