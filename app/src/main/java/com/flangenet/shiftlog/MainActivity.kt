package com.flangenet.shiftlog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //floatingActionButton.setOnClickListener {btn1Clicked()}

        btnNewShift.setOnClickListener{openNewShift()}
    }



     fun btn1Clicked() {
        Log.d("Click","Hello")

    }

    fun openNewShift(){
        val newShiftIntent = Intent(this,NewShift::class.java)
        startActivity(newShiftIntent)
    }
}
