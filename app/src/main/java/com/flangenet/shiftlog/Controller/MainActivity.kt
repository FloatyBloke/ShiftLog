package com.flangenet.shiftlog.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.EXTRA_EDIT_SHIFT
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnNewShift.setOnClickListener{openNewShift()}
        btnSettings.setOnClickListener{openSettings()}
        btnViewShifts.setOnClickListener{openViewShifts()}

    }

    private fun openSettings(){
        val settingsIntent = Intent(this, Settings::class.java)
        startActivity(settingsIntent)
    }

    private fun openNewShift(){
        val newShiftIntent = Intent(this, EditShift::class.java)
        newShiftIntent.putExtra(EXTRA_EDIT_SHIFT,0)
        startActivity(newShiftIntent)
    }

    private fun openViewShifts(){
        val listShiftsIntent = Intent(this, ListShifts::class.java)
        startActivity(listShiftsIntent)
    }
}
