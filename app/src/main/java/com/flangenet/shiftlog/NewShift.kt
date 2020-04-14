package com.flangenet.shiftlog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_shift.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

public var shiftStartDateTime: LocalDateTime = LocalDateTime.now()
public var shiftEndDateTime: LocalDateTime = LocalDateTime.now()
public val dFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
public val tFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

class NewShift : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_new_shift)
            btnShiftCancel.setOnClickListener{btnShiftCancelClicked()}
            btnShiftSave.setOnClickListener{btnSaveShiftClicked()}

            txtShiftStartDate.text = shiftStartDateTime.format(dFormatter)
            txtShiftEndDate.text = shiftEndDateTime.format(dFormatter)
            txtShiftStartTime.text = shiftStartDateTime.format(tFormatter)
            txtShiftEndTime.text = shiftEndDateTime.format(tFormatter)


        // Date and time
        val dateTime = LocalDateTime.of(2016, Month.APRIL, 15, 3, 15)
        println(dateTime)
            //dateTime = LocalDateTime.of()

        // Date only
        val date = LocalDate.of(2016, Month.APRIL, 15)
        println(date)

        // Time only
        val time = LocalTime.of(3, 15, 10)
        println(time)


        // Calendar
        val c = Calendar.getInstance()
            c.set(2019,0,7)
        val inYear = c.get(Calendar.YEAR)
        val inMonth = c.get(Calendar.MONTH)
        val inDay = c.get(Calendar.DAY_OF_MONTH)
        val cDate = c.get(Calendar.DATE)




        // Button click to show DatePickerDialog
        txtShiftStartDate.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, outYear, outMonth, outDay ->
                //val td:LocalDate = LocalDate.of(outYear,(outMonth+1),outDay)
                //txtShiftStartDate.text = td.format(dFormatter)
                shiftStartDateTime = LocalDateTime.of(outYear,outMonth,outDay, shiftStartDateTime.hour,shiftStartDateTime.minute)
                hourDiffCalc()
            }, shiftStartDateTime.year, shiftStartDateTime.monthValue, shiftStartDateTime.dayOfMonth)
            dpd.show()
            //
        }

        txtShiftEndDate.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, outYear, outMonth, outDay ->
                //val td:LocalDate = LocalDate.of(outYear,(outMonth+1),outDay)
                //txtShiftEndDate.text = td.format(dFormatter)
                shiftEndDateTime = LocalDateTime.of(outYear,outMonth,outDay,shiftEndDateTime.hour,shiftEndDateTime.minute)
                hourDiffCalc()
            }, shiftEndDateTime.year, shiftEndDateTime.monthValue, shiftEndDateTime.dayOfMonth)
            dpd.show()

        }

        txtShiftStartTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, outHour, outMinute ->
                //val tt:LocalTime = LocalTime.of(outHour,outMinute)
                //txtShiftStartTime.text = tt.format(tFormatter)
                shiftStartDateTime = LocalDateTime.of(shiftStartDateTime.year,shiftStartDateTime.monthValue,shiftStartDateTime.dayOfMonth,outHour,outMinute)
                hourDiffCalc()
            }
            TimePickerDialog(this, timeSetListener, shiftStartDateTime.hour, shiftStartDateTime.minute, true).show()
        }

        txtShiftEndTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, outHour, outMinute ->
                //val tt:LocalTime = LocalTime.of(outHour,outMinute)
                //txtShiftEndTime.text = tt.format(tFormatter)
                shiftEndDateTime = LocalDateTime.of(shiftEndDateTime.year,shiftEndDateTime.monthValue,shiftEndDateTime.dayOfMonth,outHour,outMinute)
                hourDiffCalc()
            }
            TimePickerDialog(this, timeSetListener, shiftEndDateTime.hour, shiftEndDateTime.minute, true).show()
        }



        // Spinner Setup - access the items of the list
        val breakArray = resources.getStringArray(R.array.breakChoices)

        // access the spinner
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, breakArray)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,view: View, position: Int, id: Long) {
                    Toast.makeText(this@NewShift,breakArray[position], Toast.LENGTH_SHORT).show()
                    hourDiffCalc()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }


}


    fun btnSaveShiftClicked(){
        hourDiffCalc()
    }

    fun btnShiftCancelClicked(){
        finish()
    }


    fun hourDiffCalc() {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        //var tDateTime = LocalDateTime.parse(edtShiftStart.text, formatter)


        val startDT = returnDateString("${txtShiftStartDate.text} ${txtShiftStartTime.text}:00.000")
        val endDT = returnDateString("${txtShiftEndDate.text} ${txtShiftEndTime.text}:00.000")

        val shiftStart = LocalDateTime.parse(startDT, formatter)
        val shiftEnd = LocalDateTime.parse(endDT, formatter)

        val shiftMins = Duration.between(shiftStart,shiftEnd).toMinutes()
        val shiftHours: Float = (shiftMins.toFloat()/60)
        val breakMins: Int = spinner.selectedItem.toString().toInt()
        val breakHours: Float = (breakMins.toFloat()/60)
        val shiftHoursTotal = shiftHours - breakHours
        val hourlyRate: Float = txtHourlyRate.text.toString().toFloat()
        val shiftPay: Float = shiftHoursTotal * hourlyRate




        // Update screen
        txtShiftStartDate.text = shiftStartDateTime.format(dFormatter)
        txtShiftEndDate.text = shiftEndDateTime.format(dFormatter)
        txtShiftStartTime.text = shiftStartDateTime.format(tFormatter)
        txtShiftEndTime.text = shiftEndDateTime.format(tFormatter)
        txtHours.text = shiftHoursTotal.toString()
        txtPay.text = "$shiftPay"

    }

    fun returnDateString(isoString: String) : String {
        // From ISO Date - 2017-09-11t01:16:13.858Z
        //                 YYYY-MM-DD*HH:MM:SS.SSS*
        // To Monday 4:35 PM


        // from  13/04/2020 13:30
        //       dd/MM/yyyy HH:mm:ss.SSS
        // to    2020-04-13T13:30.00.000
        //       yyyy-MM-dd'T'HH:mm:ss.SSS

        val  isoFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", Locale.getDefault())
        isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
        var convertedDate = Date()
        try {
            convertedDate = isoFormatter.parse(isoString)
        } catch (e: ParseException) {
            Log.d("PARSE", "Cannot parse date")
        }
        val outDateString = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        return outDateString.format(convertedDate)
    }



}
