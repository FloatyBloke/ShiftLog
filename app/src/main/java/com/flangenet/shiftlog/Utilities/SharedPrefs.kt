package com.flangenet.shiftlog.Utilities

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {


    var HOURLY_RATE = "hourly Rate"
    val WEEK_START_DAY = "week start day"
    var USER_NAME = "user name"
    var USER_EMAIL = "user email"
    var DATE_FORMAT = "date format"


    val PREFS_FILENAME = "prefsWL"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME,0)

    var hourlyRate: Float
        get() = prefs.getFloat(HOURLY_RATE,10F)
        set(value) = prefs.edit().putFloat(HOURLY_RATE,value).apply()

    var weekStartDay: Int
        // TODO Implement week start day
        get() = prefs.getInt(WEEK_START_DAY,1)
        set(value) = prefs.edit().putInt(WEEK_START_DAY,value).apply()
    var userName: String?
        get() = prefs.getString(USER_NAME,"User Name")
        set(value) = prefs.edit().putString(USER_NAME,value).apply()

    var userEmail: String?
        get() = prefs.getString(USER_EMAIL,"user@name.com")
        set(value) = prefs.edit().putString(USER_EMAIL,value).apply()

    var dateFormat: String?
        get() = prefs.getString(DATE_FORMAT, "dd/MM/yyyy")
        set(value) = prefs.edit().putString(DATE_FORMAT,value).apply()
}