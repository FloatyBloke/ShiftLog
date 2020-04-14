package com.flangenet.shiftlog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.flangenet.shiftlog.Model.Shift
import kotlinx.android.synthetic.main.activity_new_shift.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*



class NewShift : AppCompatActivity() {

    val dFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val tFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")


    var shift = Shift(LocalDateTime.now(),LocalDateTime.now(),0F,0F,5F,0F)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_new_shift)
            btnShiftCancel.setOnClickListener{btnShiftCancelClicked()}
            btnShiftSave.setOnClickListener{btnSaveShiftClicked()}

            // Remove Seconds and Nanos from current time
            var tTime = LocalDateTime.now()
            tTime = tTime.minusSeconds(tTime.second.toLong())
            tTime = tTime.minusNanos(tTime.nano.toLong())
            shift.start= tTime
            shift.end = tTime




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


        // Button click to show DatePickerDialog
        txtShiftStartDate.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, outYear, outMonth, outDay ->
                shift.start = LocalDateTime.of(outYear,(outMonth+1),outDay, shift.start.hour,shift.start.minute,0,0)
                mainCalc()
            }, shift.start.year, ((shift.start.monthValue)-1), shift.start.dayOfMonth)
            dpd.show()
            //
        }

        txtShiftEndDate.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, outYear, outMonth, outDay ->
                shift.end = LocalDateTime.of(outYear,(outMonth+1),outDay,shift.end.hour,shift.end.minute,0,0)
                mainCalc()
            }, shift.end.year, ((shift.end.monthValue)-1), shift.end.dayOfMonth)
            dpd.show()

        }

        txtShiftStartTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, outHour, outMinute ->
                shift.start = LocalDateTime.of(shift.start.year,shift.start.monthValue,shift.start.dayOfMonth,outHour,outMinute,0,0)
                println(shift.start)
                mainCalc()
            }
            TimePickerDialog(this, timeSetListener, shift.start.hour, shift.start.minute, true).show()
        }

        txtShiftEndTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, outHour, outMinute ->
                shift.end = LocalDateTime.of(shift.end.year,shift.end.monthValue,shift.end.dayOfMonth,outHour,outMinute,0,0)
                mainCalc()
            }
            TimePickerDialog(this, timeSetListener, shift.end.hour, shift.end.minute, true).show()
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
                    val breakMins: Int = spinner.selectedItem.toString().toInt()
                    shift.breaks = (breakMins.toFloat()/60)
                    println(shift.breaks)
                    mainCalc()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

            mainCalc()


}


    fun btnSaveShiftClicked(){
        mainCalc()
    }

    fun btnShiftCancelClicked(){
        finish()
    }


    fun mainCalc() {


        val shiftMins = Duration.between(shift.start,shift.end).toMinutes()
        println("S:${shift.start} E:${shift.end} Shift:${shiftMins}")
        val shiftHours: Float = (shiftMins.toFloat()/60)

        val breakHours: Float = shift.breaks
        val shiftHoursTotal = shiftHours - breakHours
        val hourlyRate: Float = shift.rate
        val shiftPay: Float = shiftHoursTotal * hourlyRate

        shift.hours = shiftHoursTotal
        shift.pay = shiftPay
        shift.breaks = breakHours



        // Update screen
        txtShiftStartDate.text = shift.start.format(dFormatter)
        txtShiftEndDate.text = shift.end.format(dFormatter)
        txtShiftStartTime.text = shift.start.format(tFormatter)
        txtShiftEndTime.text = shift.end.format(tFormatter)
        txtHours.text = shift.hours.toString()
        txtPay.text = "${shift.pay}"

    }





}
