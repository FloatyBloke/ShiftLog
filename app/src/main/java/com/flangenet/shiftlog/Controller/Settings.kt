package com.flangenet.shiftlog.Controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
        btnSettingsCancel.setOnClickListener{settingsCancel()}
        btnSettingsSave.setOnClickListener{settingsSave()}

        // Spinner Setup - access the items of the list
        val weekArray = resources.getStringArray(R.array.dayOfWeek)

        // access the spinner
        val spinner = findViewById<Spinner>(R.id.dowSpinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, weekArray)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    //Toast.makeText(this@NewShift,breakArray[position], Toast.LENGTH_SHORT).show()
                    println(spinner.selectedItemPosition.toString())
                    //mainCalc()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        // Spinner Setup - access the items of the list
        val dateArray = resources.getStringArray(R.array.dateFormats)

        // access the spinner
        val spinner2 = findViewById<Spinner>(R.id.dformatSpinner)
        if (spinner2 != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, dateArray)
            spinner2.adapter = adapter

            spinner2.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    //Toast.makeText(this@NewShift,breakArray[position], Toast.LENGTH_SHORT).show()
                    println(spinner2.selectedItemPosition.toString())
                    //mainCalc()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        // Set form up with saved information, placed after Spinner is created
        edtUserName.setText(App.prefs.userName)
        edtEmail.setText(App.prefs.userEmail)
        edtShiftHourlyRate.setText(App.prefs.hourlyRate.toString())
        dowSpinner.setSelection(App.prefs.weekStartDay)
        dformatSpinner.setSelection(dateArray.indexOf(App.prefs.dateFormat))









    }

    private fun settingsCancel(){
        finish()
    }

    private fun settingsSave(){
        App.prefs.userName= edtUserName.text.toString()
        App.prefs.userEmail = edtEmail.text.toString()
        App.prefs.hourlyRate = edtShiftHourlyRate.text.toString().toFloat()
        App.prefs.weekStartDay = dowSpinner.selectedItemPosition
        App.prefs.dateFormat = dformatSpinner.selectedItem.toString()
        finish()
        Toast.makeText(this,getString(R.string.settings_saved),Toast.LENGTH_LONG).show()
    }
}
