package com.flangenet.shiftlog.Controller

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.DBHelper
import com.flangenet.shiftlog.Utilities.datetimeToSQL
import com.flangenet.shiftlog.Utilities.sqlToDatetime
import kotlinx.android.synthetic.main.activity_file_i_o.*
import java.io.File
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalDateTime


class FileIO : AppCompatActivity() {


    private val filepath:String = ""
    private val outFileName:String = "shiftLogOut.txt"
    private val inFileName:String = "shiftLogIn.csv"
    internal var myExternalFile: File?=null
    lateinit var db:DBHelper

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

        enableSpinner(false)

        if (!isExternalStorageAvailable || isExternalStorageReadOnly) {
            btnExport.isEnabled = false
        }
        btnExport.setOnClickListener{writeFile()}
        //btnImport.setOnClickListener{readFile()}
        btnTemp.setOnClickListener{testStuff()}

    }


    fun writeFile() {

        // Export routine
        db = DBHelper(this)
        var lstShifts: List<DBShift> = ArrayList<DBShift>()
        lstShifts = db.getShifts(LocalDate.now(),3)
        var logData = mutableListOf<String>()

        val myExternalFile = File(getExternalFilesDir(filepath), "$outFileName")
        //val myExternalFile =File("/storage/emulated/0/Android/","$outFileName")
        println("Export Filename : ${myExternalFile!!.absoluteFile.toString()}")

        enableSpinner(true)

        myExternalFile.printWriter().use { out ->
            lstShifts.forEach{
            var outputData =
                "${it.id.toString()},${datetimeToSQL(it.start!!)},${datetimeToSQL(it.end!!)},${it.breaks.toString()},${it.hours.toString()},${it.rate.toString()},${it.pay.toString()}"
            out.println(outputData)
            logData.add(outputData + System.lineSeparator())

            }
        }
        db.close()
        //logText.text = logData.toString()
        logText.setText(logData.toString())
        enableSpinner(false)
    }



    fun readFile(fileName:String){
        //println(importFile.absoluteFile)

        //"content://com.android.externalstorage.documents/document/primary:Android/data/com.flangenet.shiftlog/files/Stash/exshifts.csv"
        val lineList = mutableListOf<String>()



        try {
            myExternalFile = File(getExternalFilesDir(filepath),inFileName)
            myExternalFile= File(fileName)
            println("Import Filename : ${myExternalFile!!.absoluteFile.toString()}")
            val inputStream: InputStream = myExternalFile!!.inputStream()
            //inputStream = contentResolver.open
            inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }
        } catch (e:Exception){
            Toast.makeText(this,"Import Error",Toast.LENGTH_SHORT).show()
        }


        var word:String = "eagle,falcon,hawk,owl"
        var importLine = word.split(",")


        var db = DBHelper(this)

        //******************************
        //db.deleteTable()
        //db.createTable()
        //******************************
        logText.setText(lineList.toString())

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


                //db.addShift(iShift)
            } catch (e:Exception){
                println("Parse Error : *${importLine[0]}*${importLine[1]}*${importLine[2]}*${importLine[3]}*${importLine[4]}*${importLine[5]}*${importLine[6]}*")
            }


            println(it.toString())

        }
    println("I'm out")

    }
    fun enableSpinner(enable : Boolean){
        if (enable){
            progressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.INVISIBLE
        }
        btnImport.isEnabled  = !enable
        btnExport.isEnabled = !enable
        logText.isEnabled = !enable
        hideKeyboard()



    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }


    fun testStuff(){


            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            println(selectedFile)


            if (selectedFile != null) {
                println(selectedFile.path)
                //readFile("file://${selectedFile.path}")
                getFileName(this,selectedFile)?.let { readFile2(it) }
            }
        }
    }

    fun readFile2(fileName: String) {
        println(fileName)
        val file = File(fileName)
        file.forEachLine { println(it) }
    }

    private fun getFileName(
        context: Context,
        uri: Uri
    ): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf(File.separator)
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}



