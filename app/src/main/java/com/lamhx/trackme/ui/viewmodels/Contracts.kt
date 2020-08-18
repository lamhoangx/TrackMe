package com.lamhx.trackme.ui.viewmodels

import androidx.annotation.IntDef

class Contracts {
    /**
     * Workout state
     */
    @IntDef(start, running, pause, stop)
    annotation class WorkoutState
    companion object {
        //Workout state
        const val start: Int = 0
        const val running: Int = 1
        const val pause: Int = 2
        const val stop: Int = -1
    }
}