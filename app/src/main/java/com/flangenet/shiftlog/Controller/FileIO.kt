package com.flangenet.shiftlog.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.flangenet.shiftlog.R
import kotlinx.android.synthetic.main.activity_file_i_o.*
import java.io.*

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
        myExternalFile = File(getExternalFilesDir(filepath), fileName)
        try {
            val fileOutPutStream = FileOutputStream(myExternalFile)
            fileOutPutStream.write(outputData.toByteArray())
            fileOutPutStream.close()
            logText.text = outputData.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Toast.makeText(applicationContext,"data save", Toast.LENGTH_SHORT).show()
        println( myExternalFile)
    }



    fun readFile(){

        myExternalFile = File(getExternalFilesDir(filepath),fileName)
        println("Hello ***********************")



        val inputStream: InputStream = myExternalFile!!.inputStream()
        val inputString = inputStream.bufferedReader().use { it.readText() }

        val lineList = mutableListOf<String>()

        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }
        lineList.forEach{println(">  " + it)}

        //println(inputString)
        logText.text= lineList.toString()
    }
}



