package com.lamhx.trackme.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.lamhx.trackme.R
import com.lamhx.trackme.databinding.RecordWorkoutActivityBinding
import com.lamhx.trackme.databinding.TrackMeActivityBinding

class RecordWorkoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<RecordWorkoutActivityBinding>(
            this,
            R.layout.record_workout_activity
        )
    }

    override fun onResume() {
        super.onResume()
    }

    fun finishWorkout() {
        if(isTaskRoot) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            finishAndRemoveTask()
            startActivity(intent)
        } else {
            finish()
        }
    }


}