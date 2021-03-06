package com.flangenet.shiftlog.Controller

//import java.time.LocalDate

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.DBHelper
import com.flangenet.shiftlog.Utilities.RealPathUtil
import com.flangenet.shiftlog.Utilities.datetimeToSQL
import com.flangenet.shiftlog.Utilities.removeBrackets
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_file_i_o.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import java.io.File
import java.io.InputStream
import java.io.OutputStream


class FileIO : AppCompatActivity() {

    private lateinit var mAdView : AdView

    private lateinit var db: DBHelper
    private val outputIntent = Intent(ACTION_CREATE_DOCUMENT)
    private val inputIntent = Intent(ACTION_GET_CONTENT)


    private val WRITE_EXTERNAL_REQUEST_CODE = 113
    private val READ_EXTERNAL_REQUEST_CODE = 114

    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


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

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        enableSpinner(false, getString(R.string.message_export))

        verifyStoragePermissions(this)

/*
        // Check for permission to write to external storage and ask if not
        val permissionW = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permissionW != PackageManager.PERMISSION_GRANTED) {
            Log.i("Permissions", "Permission to WRITE denied")
            makeRequest(WRITE_EXTERNAL_REQUEST_CODE)
        }

        val permissionR = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permissionR != PackageManager.PERMISSION_GRANTED) {
            Log.i("Permissions", "Permission to READ denied")
            makeRequest(READ_EXTERNAL_REQUEST_CODE)
        }

*/
        if (!isExternalStorageAvailable || isExternalStorageReadOnly) {
            btnExport.isEnabled = false
        }
        btnExport.setOnClickListener { writeFile() }
        btnImport.setOnClickListener { readFile() }




    }

    /*
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            WRITE_EXTERNAL_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("Permissions", "Write Permission has been denied by user")
                } else {
                    Log.i("Permissions", "Write Permission has been granted by user")
                }
            }
            READ_EXTERNAL_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("Permissions", "Read Permission has been denied by user")
                } else {
                    Log.i("Permissions", "Read Permission has been granted by user")
                }
            }
        }
    }
*/
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
        outputIntent.addCategory(CATEGORY_OPENABLE)
        outputIntent.putExtra(EXTRA_LOCAL_ONLY, true)

        // REQUEST_CODE = <some-integer>
        startActivityForResult(outputIntent, 112)

    }

     private fun readFile() {
        //val intent = Intent(Intent.ACTION_GET_CONTENT);

        // Update with mime types
        inputIntent.type = "*/*"
         inputIntent.flags = FLAG_GRANT_WRITE_URI_PERMISSION
         inputIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
         inputIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
         inputIntent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
         inputIntent.addFlags(FLAG_GRANT_PREFIX_URI_PERMISSION)

        // Update with additional mime types here using a String[].
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Only pick openable and local files. Theoretically we could pull files from google drive
        // or other applications that have networked files, but that's unnecessary for this example.
        inputIntent.addCategory(CATEGORY_OPENABLE)
        inputIntent.putExtra(EXTRA_LOCAL_ONLY, true)

        // REQUEST_CODE = <some-integer>
        startActivityForResult(inputIntent, 111)
        // startActivityForResult(Intent.createChooser(inputIntent, "Select a file"), 111)

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
                //val selectedPath = data.getFilePath(this)
                //
                //println("Filename last path seg - ${intent.data?.lastPathSegment?.substringAfterLast(":")}")

                // This bits works but may not be as backward compatible as the RealPathUtil (getFilePath)
                // uri?.lastPathSegment?.substringAfterLast(":")

                importTable(uri!!)
                //importTable(data.data.getFilePath(this))

            }
        }
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
                "${datetimeToSQL(it.start!!)},${datetimeToSQL(it.end!!)},${it.breaks.toString()},${it.hours.toString()},${it.rate.toString()},${it.pay.toString()},${it.tips.toString()}"
            logData.add(System.lineSeparator() + outputData)
        }
        logText.setText(logData.toString())

        try {
            val output: OutputStream? = uri.let { contentResolver.openOutputStream(it) }
            output?.bufferedWriter().use { it?.write(removeBrackets(logData.toString())) }
            output?.close()

            Toast.makeText(this, getString(R.string.message_export_complete), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "${getString(R.string.message_export_error)} ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            println(e.message)
        }
        enableSpinner(false, getString(R.string.message_export))
        logText.setText(logData.toString())
    }

    private fun importTable(uri: Uri) {
        db = DBHelper(this)
        enableSpinner(true, getString(R.string.message_import))
        val lineList = mutableListOf<String>()

        try{

            File(uri.getFilePath(this)).forEachLine {

                //println(it)
                lineList.add(it)
                logText.append(System.lineSeparator() + it)

            }

            } catch (e: Exception){
                println("Error : Importing text file ${uri.getFilePath(this)} ${e.message}")
        }


        GlobalScope.launch(Dispatchers.Main) {
            val errorTemp = async(Dispatchers.IO) {db.importShifts(lineList)}
            importComplete(errorTemp.await(), lineList.count())
        }
    }

    private fun importTableOld(uri: Uri) {

        val lineList = mutableListOf<String>()

        db = DBHelper(this)
        enableSpinner(true, getString(R.string.message_import))
        val inputStream: InputStream? = uri.let { contentResolver.openInputStream(it) }
        inputStream?.bufferedReader()?.useLines { lines -> lines.forEach {
            lineList.add(it)
            logText.append(it + "\n")

        } }
        inputStream?.close()
         //logText.append()
        //logText.setText(log)


         //Shove import routine to another thread

        GlobalScope.launch(Dispatchers.Main) {
            val errorTemp = async(Dispatchers.IO) {db.importShifts(lineList)}
            importComplete(errorTemp.await(), lineList.count())
        }
     }

    private fun importComplete(errorCount: Int, lineCount: Int) {
        val logInfo = "${getString(R.string.message_import_complete)} : ${lineCount - errorCount}/${lineCount} ${getString(
            R.string.import_summary_text
        )} : $errorCount"
        Toast.makeText(applicationContext, logInfo, Toast.LENGTH_LONG).show()
        enableSpinner(false, getString(R.string.message_import_complete))
        logText.append(System.lineSeparator() + logInfo)
        //db.close()
    }


    // Extension on intent functions to enable filename extraction from URI

    fun Intent?.getFilePath(context: Context): String {
        return this?.data?.let { data -> RealPathUtil.getRealPath(context, data) ?: "" } ?: ""
    }

    private fun Uri?.getFilePath(context: Context): String {
        return this?.let { uri -> RealPathUtil.getRealPath(context, uri) ?: "" } ?: ""
    }

    fun ClipData.Item?.getFilePath(context: Context): String {
        return this?.uri?.getFilePath(context) ?: ""
    }

    // Usage
    //val selectedPath = intent.getFilePath(context)




    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    private fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission =
            ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

}






