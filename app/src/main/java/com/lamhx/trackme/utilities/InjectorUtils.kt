package com.lamhx.trackme.utilities

import com.lamhx.trackme.data.AppDatabase
import com.lamhx.trackme.ui.viewmodels.MainViewModelFactory
import com.lamhx.trackme.ui.viewmodels.TrackMeSessionViewModelFactory
import com.lamhx.trackme.ui.viewmodels.WorkoutHistoryViewModel

object InjectorUtils {
    fun provideMainViewModelFactory(): MainViewModelFactory {
        return MainViewModelFactory(AppDatabase.getInstance().getWorkoutRepository())
    }

    fun provideTrackMeSessionViewModelFactory(): TrackMeSessionViewModelFactory {
        return TrackMeSessionViewModelFactory(AppDatabase.getInstance().getWorkoutRepository())
    }

    fun provideWorkoutHistoryViewModel(workoutId: Long): WorkoutHistoryViewModel {
        return WorkoutHistoryViewModel(AppDatabase.getInstance().getWorkoutRepository(), workoutId)
    }
}