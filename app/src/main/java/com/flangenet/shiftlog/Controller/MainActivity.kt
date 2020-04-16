package com.flangenet.shiftlog.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.flangenet.shiftlog.R
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
        startActivity(newShiftIntent)
    }

    fun openViewShifts(){

        //val t = LocalDateTime.parse("2007-12-03 10:15:30.999", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        //Toast.makeText(this,"${t.toString()}",Toast.LENGTH_LONG).show()



        //Toast.makeText(this,dateToSqlDate(LocalDateTime.now()),Toast.LENGTH_LONG).show()

        val listShiftsIntent = Intent(this, ListShifts::class.java)
        startActivity(listShiftsIntent)

    }

    fun dateToSqlDate(inDate: LocalDateTime) : String{
        val t = inDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

        return t
    }
}
