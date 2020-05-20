package com.flangenet.shiftlog.Controller

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.DBHelper
import com.flangenet.shiftlog.Utilities.datetimeToSQL
import kotlinx.android.synthetic.main.activity_file_i_o.*
import java.io.InputStream
import java.io.OutputStream
//import java.time.LocalDate

import kotlinx.coroutines.*
import org.joda.time.LocalDate


class FileIO : AppCompatActivity() {

    private lateinit var db: DBHelper
    private val outputIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
    private val inputIntent = Intent(Intent.ACTION_GET_CONTENT)

    private val isExternalStorageReadOnly: Boolean
        get() {
            val extStorageState = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
        }
    private val isExternalStorageAvailable: Boolean
        get() {
            val extStorageState = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == extStorageState
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_i_o)
        logText.setText(R.string.file_information)

        enableSpinner(false,getString(R.string.message_export))

        if (!isExternalStorageAvailable || isExternalStorageReadOnly) {
            btnExport.isEnabled = false
        }
        btnExport.setOnClickListener { writeFile() }
        btnImport.setOnClickListener { readFile() }
    }

    private fun enableSpinner(enable: Boolean, info: String) {
        txtInfo.text = info
        if (enable) {
            progressBar.visibility = View.VISIBLE
            txtInfo.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
            txtInfo.visibility = View.INVISIBLE
        }

        btnImport.isEnabled = !enable
        btnExport.isEnabled = !enable
        logText.isEnabled = !enable
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }


     private fun writeFile() {
        //val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        //val intent =  Intent(Intent.ACTION_GET_CONTENT)

        // Update with mime types
        outputIntent.type = "*/*"
        outputIntent.flags = FLAG_GRANT_WRITE_URI_PERMISSION
        outputIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
        outputIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        outputIntent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        outputIntent.addFlags(FLAG_GRANT_PREFIX_URI_PERMISSION)

        // Update with additional mime types here using a String[].
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Only pick openable and local files. Theoretically we could pull files from google drive
        // or other applications that have networked files, but that's unnecessary for this example.
        outputIntent.addCategory(Intent.CATEGORY_OPENABLE)
        outputIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)

        // REQUEST_CODE = <some-integer>
        startActivityForResult(outputIntent, 112)

    }

     private fun readFile() {
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
            }
        }

        // 111 is an import
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            if (selectedFile != null) {
                val uri = data.data
                importTable(uri!!)
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
        val nowDate = LocalDate()
        val lstShifts: List<DBShift> = db.getShifts(nowDate, 3)
        db.close()

        val logData = mutableListOf<String>()

        enableSpinner(true, getString(R.string.message_export))

        lstShifts.forEach {
            val outputData =
                "${datetimeToSQL(it.start!!)},${datetimeToSQL(it.end!!)},${it.breaks.toString()},${it.hours.toString()},${it.rate.toString()},${it.pay.toString()}"
            logData.add(System.lineSeparator() + outputData)
        }
        logText.setText(logData.toString())

        try {
            val output: OutputStream? = uri.let { contentResolver.openOutputStream(it) }
            output?.bufferedWriter().use { it?.write(logData.toString()) }
            output?.close()

            Toast.makeText(this, "Export Complete", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Export Error ${e.message}", Toast.LENGTH_SHORT).show()
            println(e.message)
        }
        enableSpinner(false, getString(R.string.message_export))
        logText.setText(logData.toString())
    }

     private fun importTable(uri: Uri) {

        val lineList = mutableListOf<String>()
        db = DBHelper(this)
        enableSpinner(true, getString(R.string.message_import))
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

    private fun importComplete(errorCount:Int, lineCount:Int) {
        Toast.makeText(applicationContext,"Import Complete : ${lineCount - errorCount}/${lineCount} Lines - Errors : $errorCount",Toast.LENGTH_LONG).show()
        enableSpinner(false,getString(R.string.message_import))
    }
}






