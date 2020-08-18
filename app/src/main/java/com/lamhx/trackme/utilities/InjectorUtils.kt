package com.lamhx.trackme.utilities

import com.lamhx.trackme.data.AppDatabase
import com.lamhx.trackme.ui.viewmodels.MainViewModelFactory
import com.lamhx.trackme.ui.viewmodels.TrackMeSessionViewModelFactory

object InjectorUtils {
    fun provideMainViewModelFactory(): MainViewModelFactory {
        return MainViewModelFactory(AppDatabase.getInstance().getWorkoutRepository())
    }

    fun provideTrackMeSessionViewModelFactory(): TrackMeSessionViewModelFactory {
        return TrackMeSessionViewModelFactory(AppDatabase.getInstance().getWorkoutRepository())
    }
}