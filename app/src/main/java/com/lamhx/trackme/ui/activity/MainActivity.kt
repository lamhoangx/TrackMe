package com.lamhx.trackme.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.lamhx.trackme.R
import com.lamhx.trackme.databinding.TrackMeActivityBinding
import com.lamhx.trackme.utilities.PermissionUtils.PERMISSION_REQUEST_CODE_MAIN_FRAGMENT_FINE_LOCATION
import com.lamhx.trackme.utilities.PermissionUtils.checkSelfPermissionCompat
import com.lamhx.trackme.utilities.PermissionUtils.requestPermissionsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView<TrackMeActivityBinding>(
            this,
            R.layout.track_me_activity
        )
        ensurePermission()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        //trigger resume event to fragment
        intent = intent
    }

    /**
     * Check permission will using on app
     */
    private fun ensurePermission() {
        val arrayPermissionMustRequest: ArrayList<String> = ArrayList()
        if (checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            arrayPermissionMustRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (checkSelfPermissionCompat(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            arrayPermissionMustRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (arrayPermissionMustRequest.size > 0) {
            requestPermissionsCompat(
                arrayPermissionMustRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE_MAIN_FRAGMENT_FINE_LOCATION
            )
        }
    }
}