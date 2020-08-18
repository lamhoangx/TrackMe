package com.lamhx.trackme.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lamhx.trackme.data.WorkoutRepository

/**
 * Factory for creating a [MainViewModel]
 */
class MainViewModelFactory(
    private val workoutRepository: WorkoutRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(workoutRepository) as T
    }

}