package com.lamhx.trackme.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.lamhx.trackme.R
import com.lamhx.trackme.databinding.RecordWorkoutActivityBinding

class WorkoutHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<RecordWorkoutActivityBinding>(
            this,
            R.layout.activity_workout_history
        )
    }
}