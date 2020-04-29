package com.flangenet.shiftlog.Controller

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.DBHelper
import com.flangenet.shiftlog.Utilities.datetimeToSQL
import com.flangenet.shiftlog.Utilities.removeBrackets
import com.flangenet.shiftlog.Utilities.sqlToDatetime
import kotlinx.android.synthetic.main.activity_file_i_o.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.*



class FileIO : AppCompatActivity() {

    private val filepath: String = ""
    private val outFileName: String = "shiftLogOut.txt"
    private val inFileName: String = "shiftLogIn.csv"
    internal var myExternalFile: File? = null
    lateinit var db: DBHelper
    lateinit var outputUri: Uri
    val outputIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
    val inputIntent = Intent(Intent.ACTION_GET_CONTENT);



    private val isExternalStorageReadOnly: Boolean
        get() {
            val extStorageState = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)
        }
    private val isExternalStorageAvailable: Boolean
        get() {
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
        btnExport.setOnClickListener { writeFile() }
        btnImport.setOnClickListener { readFile() }
        btnTemp.setOnClickListener{testing()}
        //outputUri.path = "content://com.android.providers.downloads.documents/document/raw%3A%2Fstorage%2Femulated%2F0%2FDownload%2Fcleanshifts.csv"

    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
        }
        btnImport.isEnabled = !enable
        btnExport.isEnabled = !enable
        logText.isEnabled = !enable
        hideKeyboard()
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }


     fun writeFile() {
        //val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        //val intent =  Intent(Intent.ACTION_GET_CONTENT)

        // Update with mime types
        outputIntent.type = "*/*";
        outputIntent.flags = FLAG_GRANT_WRITE_URI_PERMISSION
        outputIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
        outputIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        outputIntent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        outputIntent.addFlags(FLAG_GRANT_PREFIX_URI_PERMISSION)

        // Update with additional mime types here using a String[].
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Only pick openable and local files. Theoretically we could pull files from google drive
        // or other applications that have networked files, but that's unnecessary for this example.
        outputIntent.addCategory(Intent.CATEGORY_OPENABLE);
        outputIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // REQUEST_CODE = <some-integer>
        startActivityForResult(outputIntent, 112);

    }

     fun readFile() {
        //val intent = Intent(Intent.ACTION_GET_CONTENT);

        // Update with mime types
        inputIntent.type = "*/*"

        // Update with additional mime types here using a String[].
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Only pick openable and local files. Theoretically we could pull files from google drive
        // or other applications that have networked files, but that's unnecessary for this example.
        inputIntent.addCategory(Intent.CATEGORY_OPENABLE)
        inputIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)

        // REQUEST_CODE = <some-integer>
        startActivityForResult(inputIntent, 111)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 112 is an export
        if (requestCode == 112 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file

            if (selectedFile != null) {
                val uri = data.data
                exportTable(uri!!)
                //logText.setText(uri.toString())
            }

        }

        // 111 is an import
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            if (selectedFile != null) {
                val uri = data.data
                importTable(uri!!)
                //logText.setText(uri.toString())

            }
        }
    }

    private fun deleteAllShifts() {
        db = DBHelper(this)
        db.emptyTable(this)
        db.close()
    }

    private fun testing(){
        db = DBHelper(this)
        db.getPath()
        db.close()
    }

    private fun exportTable(uri: Uri){
        db = DBHelper(this)
        var lstShifts: List<DBShift> = ArrayList<DBShift>()
        lstShifts = db.getShifts(LocalDate.now(), 3)
        db.close()

        var logData = mutableListOf<String>()

        enableSpinner(true)

        lstShifts.forEach {
            var outputData =
                "${it.id.toString()},${datetimeToSQL(it.start!!)},${datetimeToSQL(it.end!!)},${it.breaks.toString()},${it.hours.toString()},${it.rate.toString()},${it.pay.toString()}"
            logData.add(System.lineSeparator() + outputData)
            println(outputData)
        }
        logText.setText(logData.toString())

        try {
            val output: OutputStream? =
                uri.let { contentResolver.openOutputStream(it) }
            val outputAsString =
                output?.bufferedWriter().use { it?.write(logData.toString()) }
            output?.close()

            Toast.makeText(this, "Export Complete", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Export Error ${e.message}", Toast.LENGTH_SHORT).show()
            println(e.message)
        }
        enableSpinner(false)
        logText.setText(logData.toString())
    }

     private fun importTable(uri: Uri) {

        val lineList = mutableListOf<String>()
        var iShift: DBShift = DBShift(0, LocalDateTime.now(), LocalDateTime.now(), 0F, 0F, 0F, 0F)
        var importLine: List<String>
        var logData = StringBuilder()
        var errorCount = 0
        db = DBHelper(this)

        enableSpinner(true)
        //GlobalScope.launch {

        val inputStream: InputStream? = uri.let { contentResolver.openInputStream(it) }
        inputStream?.bufferedReader()?.useLines { lines -> lines.forEach { lineList.add(it) } }
        inputStream?.close()
        logText.setText(lineList.toString())

         //Shove import routine to another thread
        GlobalScope.launch(Dispatchers.Main) {
            val errorTemp = async(Dispatchers.IO) {db.importShifts(lineList)}
            importComplete(errorTemp.await(),lineList.count())
        }


    }

    fun importComplete(errorCount:Int,lineCount:Int) {
        println("*******I'm back")
        //db.close()
        Toast.makeText(applicationContext,"Import Complete : ${lineCount - errorCount}/${lineCount} Lines - Errors : $errorCount",Toast.LENGTH_LONG).show()
        //Toast.makeText(applicationContext,"Import Complete - Errors : $errorCount",Toast.LENGTH_LONG).show()
        enableSpinner(false)

    }

}






