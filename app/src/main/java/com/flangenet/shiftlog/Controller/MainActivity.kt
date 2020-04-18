package com.flangenet.shiftlog.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.EXTRA_EDIT_SHIFT
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //floatingActionButton.setOnClickListener {btn1Clicked()}

        btnNewShift.setOnClickListener{openNewShift()}
        btnSettings.setOnClickListener{openSettings()}
        btnViewShifts.setOnClickListener{openViewShifts()}
    }

    fun openSettings(){
        val settingsIntent = Intent(this, Settings::class.java)
        startActivity(settingsIntent)
    }

    fun openNewShift(){
        val newShiftIntent = Intent(this, NewShift::class.java)
        newShiftIntent.putExtra(EXTRA_EDIT_SHIFT,0)
        startActivity(newShiftIntent)
    }

    fun openViewShifts(){
        val listShiftsIntent = Intent(this, ListShifts::class.java)
        startActivity(listShiftsIntent)
    }

}
