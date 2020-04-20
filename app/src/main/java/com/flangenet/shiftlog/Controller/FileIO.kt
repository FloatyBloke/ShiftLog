package com.flangenet.shiftlog.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.DBHelper
import com.flangenet.shiftlog.Utilities.sqlToDatetime
import kotlinx.android.synthetic.main.activity_file_i_o.*
import java.io.*
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FileIO : AppCompatActivity() {


    private val filepath:String = "Stash"
    private val fileName:String = "shifts.csv"
    internal var myExternalFile: File?=null

    private val isExternalStorageReadOnly: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)
    }
    private val isExternalStorageAvailable: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED.equals(extStorageState)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_i_o)
        if (!isExternalStorageAvailable || isExternalStorageReadOnly) {
            btnExport.isEnabled = false
        }
        btnExport.setOnClickListener{writeFile()}
        btnImport.setOnClickListener{readFile()}
    }


    fun writeFile() {

        val outputData : String = "Suck My Balls"
        myExternalFile = File(getExternalFilesDir(filepath), "ex$fileName")
        println("Hello ***********************${myExternalFile!!.absoluteFile.toString()}")
        try {
            val fileOutPutStream = FileOutputStream(myExternalFile)
            fileOutPutStream.write(outputData.toByteArray())
            fileOutPutStream.close()
            logText.text = outputData.toString()
            Toast.makeText(applicationContext,"data save", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        println( myExternalFile)
    }



    fun readFile(){

        val lineList = mutableListOf<String>()



        try {
            println("Hello ***********************")
            myExternalFile = File(getExternalFilesDir(filepath),fileName)
            println("Hello ***********************${myExternalFile!!.absoluteFile.toString()}")
            val inputStream: InputStream = myExternalFile!!.inputStream()
            inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }
        } catch (e:Exception){
            Toast.makeText(this,"Import Error",Toast.LENGTH_SHORT)
        }


        var word:String = "eagle,falcon,hawk,owl"
        var importLine = word.split(",")


        var db = DBHelper(this)


        //******************************
        //db.deleteTable()
        //db.createTable()
        //******************************

        var iShift:DBShift = DBShift(0,LocalDateTime.now(),LocalDateTime.now(),0F,0F,0F,0F)


        lineList.forEach{
            importLine = it.split(",")

            //println(importLine[0])
            try {
                iShift.id = importLine[0].toInt()
                iShift.start = sqlToDatetime(importLine[1])
                iShift.end = sqlToDatetime(importLine[2])
                iShift.breaks = importLine[3].toFloat()
                iShift.hours = importLine[4].toFloat()
                iShift.rate = importLine[5].toFloat()
                iShift.pay = importLine[6].toFloat()
                db.addShift(iShift)
            } catch (e:Exception){
                println("Parse Error : *${importLine[0]}*${importLine[1]}*${importLine[2]}*${importLine[3]}*${importLine[4]}*${importLine[5]}*${importLine[6]}*")
            }


            //db.importShift(importLine[1],importLine[2],importLine[3].toFloat(),importLine[4].toFloat(),importLine[5].toFloat(),importLine[6].toFloat())

        }

        logText.text= lineList.toString()
    }

}



