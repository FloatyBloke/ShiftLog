package com.flangenet.shiftlog.Controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.flangenet.shiftlog.R
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        // Set Button Listeners
        //btnSettingsCancel.setOnClickListener{settingsCancel()}
        btnSettingsSave.setOnClickListener{settingsSave()}

        btnFiles.setOnClickListener{
            val fileIntent = Intent(this,FileIO::class.java)
            fileIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            fileIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            fileIntent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            startActivity(fileIntent)

        }

        // Day of week Spinner Setup
        val weekArray = resources.getStringArray(R.array.dayOfWeek)
        val spinner = findViewById<Spinner>(R.id.dowSpinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, weekArray)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    // Code for action
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // code for no action
                }
            }
        }

        // Date Format Spinner Setup
        val dateArray = resources.getStringArray(R.array.dateFormats)
        val spinner2 = findViewById<Spinner>(R.id.dformatSpinner)
        if (spinner2 != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, dateArray)
            spinner2.adapter = adapter
            spinner2.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    // Code for action
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // code for no action
                }
            }
        }

        // Date Format Spinner Setup
        val timeArray = resources.getStringArray(R.array.timeFormats)
        val spinner3 = findViewById<Spinner>(R.id.tformatSpinner)
        if (spinner3 != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, timeArray)
            spinner3.adapter = adapter
        }

        // Set form up with saved information, placed after Spinner is created
        edtUserName.setText(App.prefs.userName)
        edtEmail.setText(App.prefs.userEmail)
        edtShiftHourlyRate.setText(App.prefs.hourlyRate.toString())
        dowSpinner.setSelection(App.prefs.weekStartDay)
        dformatSpinner.setSelection(dateArray.indexOf(App.prefs.dateFormat))
        tformatSpinner.setSelection(dateArray.indexOf(App.prefs.timeFormat))
        hideKeyboard()
    }

    private fun settingsSave(){
        App.prefs.userName= edtUserName.text.toString()
        App.prefs.userEmail = edtEmail.text.toString()
        App.prefs.hourlyRate = edtShiftHourlyRate.text.toString().toFloat()
        App.prefs.weekStartDay = dowSpinner.selectedItemPosition
        App.prefs.dateFormat = dformatSpinner.selectedItem.toString()
        App.prefs.timeFormat = tformatSpinner.selectedItem.toString()
        finish()
        Toast.makeText(this,getString(R.string.settings_saved),Toast.LENGTH_LONG).show()
    }
    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }

}
