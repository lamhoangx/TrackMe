package com.lamhx.trackme

import android.app.Application
import android.content.Context

class MainApplication : Application() {
    companion object {
        private var context: Context? = null
        fun getAppContext(): Context {
            return context!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}