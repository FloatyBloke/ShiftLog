package com.flangenet.shiftlog.Controller

import android.app.Application
import com.flangenet.shiftlog.Utilities.SharedPrefs




    class App  : Application(){

        companion object {
            lateinit var prefs: SharedPrefs

        }
        override fun onCreate() {
            prefs = SharedPrefs(applicationContext)
            super.onCreate()
        }
    }


