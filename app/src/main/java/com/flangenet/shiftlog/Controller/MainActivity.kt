package com.flangenet.shiftlog.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.flangenet.shiftlog.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //floatingActionButton.setOnClickListener {btn1Clicked()}

        btnNewShift.setOnClickListener{openNewShift()}
        btnSettings.setOnClickListener{openSettings()}
    }



    fun openSettings(){
        val settingsIntent = Intent(this, Settings::class.java)
        startActivity(settingsIntent)
    }


    fun openNewShift(){
        val newShiftIntent = Intent(this, NewShift::class.java)
        startActivity(newShiftIntent)
    }
}
