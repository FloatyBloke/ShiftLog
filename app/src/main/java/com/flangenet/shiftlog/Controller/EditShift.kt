package com.flangenet.shiftlog.Controller

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.Model.Shift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.DBHelper
import com.flangenet.shiftlog.Utilities.EXTRA_EDIT_SHIFT
import kotlinx.android.synthetic.main.activity_new_shift.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter




class EditShift : AppCompatActivity() {
    internal lateinit var db: DBHelper
    var shiftID: Int = 0
    var shift = Shift(LocalDateTime.now(),LocalDateTime.now(),0F,0F,5F,0F)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_shift)

        shiftID = intent.getIntExtra(EXTRA_EDIT_SHIFT,0)
        if (shiftID > 0) { txtNewShiftTitle.text = "Edit Shift"}
        println("Shift To Process : $shiftID")
        btnShiftCancel.setOnClickListener{btnShiftCancelClicked()}
        btnShiftSave.setOnClickListener{btnSaveShiftClicked()}
        btnShiftDelete.setOnClickListener{btnShiftDeleteClicked()}

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
                    //Toast.makeText(this@NewShift,breakArray[position], Toast.LENGTH_SHORT).show()
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




        db = DBHelper(this)

        if (shiftID == 0){
            // Set fields for new record
            // Remove Seconds and Nanos from current time
            var tTime = LocalDateTime.now()
            tTime = tTime.minusSeconds(tTime.second.toLong())
            tTime = tTime.minusNanos(tTime.nano.toLong())

            shift.start= tTime
            shift.end = tTime
            shift.breaks = 0F
            shift.rate = App.prefs.hourlyRate
        } else {
            val tShift:DBShift = db.getShift(shiftID)
            shift.start = tShift.start!!
            shift.end = tShift.end!!
            shift.breaks = tShift.breaks!!
            shift.hours = tShift.hours!!
            shift.rate = tShift.rate!!
            shift.pay = tShift.pay!!
        }
        val t= ((shift.breaks)*60).toInt()
        spinner.setSelection(breakArray.indexOf(t.toString()))

        println("BreakMins $t")



        // Button click to show DatePickerDialog
        txtShiftStartDate.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, outYear, outMonth, outDay ->
                shift.start = LocalDateTime.of(outYear,(outMonth+1),outDay, shift.start.hour,shift.start.minute,0,0)
                mainCalc()
            }, shift.start.year, ((shift.start.monthValue)-1), shift.start.dayOfMonth)
            dpd.show()
        }

        txtShiftEndDate.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, outYear, outMonth, outDay ->
                shift.end = LocalDateTime.of(outYear,(outMonth+1),outDay,shift.end.hour,shift.end.minute,0,0)
                mainCalc()
            }, shift.end.year, ((shift.end.monthValue)-1), shift.end.dayOfMonth)
            dpd.show()
        }

        txtShiftStartTime.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, outHour, outMinute ->
                shift.start = LocalDateTime.of(shift.start.year,shift.start.monthValue,shift.start.dayOfMonth,outHour,outMinute,0,0)
                mainCalc()
            }
            TimePickerDialog(this, timeSetListener, shift.start.hour, shift.start.minute, true).show()
        }

        txtShiftEndTime.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, outHour, outMinute ->
                shift.end = LocalDateTime.of(shift.end.year,shift.end.monthValue,shift.end.dayOfMonth,outHour,outMinute,0,0)
                mainCalc()
            }
            TimePickerDialog(this, timeSetListener, shift.end.hour, shift.end.minute, true).show()
        }


        mainCalc()


    }

    fun mainCalc() {
        // Calculations to update information

        val shiftMins = Duration.between(shift.start,shift.end).toMinutes()
        //println("S:${shift.start} E:${shift.end} Shift:${shiftMins}")
        val shiftHours: Float = (shiftMins.toFloat()/60)

        val breakHours: Float = shift.breaks
        val shiftHoursTotal = shiftHours - breakHours
        val hourlyRate: Float = shift.rate
        val shiftPay: Float = shiftHoursTotal * hourlyRate

        shift.hours = shiftHoursTotal
        shift.pay = shiftPay
        //shift.breaks = breakHours

        // Update screen
        txtShiftStartDate.text = shift.start.format(DateTimeFormatter.ofPattern(App.prefs.dateFormat))
        txtShiftEndDate.text = shift.end.format(DateTimeFormatter.ofPattern(App.prefs.dateFormat))
        txtShiftStartTime.text = shift.start.format(DateTimeFormatter.ofPattern(App.prefs.timeFormat))
        txtShiftEndTime.text = shift.end.format(DateTimeFormatter.ofPattern(App.prefs.timeFormat))
        txtHours.text = shift.hours.toString()
        txtShiftHourlyRate.text = shift.rate.toString()
        txtPay.text = "${shift.pay}"

    }

    fun btnSaveShiftClicked(){
        if (shiftID == 0){
            val passShift = DBShift(1, shift.start,shift.end,shift.breaks,shift.hours,shift.rate,shift.pay)
            db.addShift(passShift)
        } else {
            val passShift = DBShift(shiftID, shift.start,shift.end,shift.breaks,shift.hours,shift.rate,shift.pay)
            db.updateShift(passShift)
        }

        finish()

    }

    fun btnShiftCancelClicked(){
        finish()
    }

    fun btnShiftDeleteClicked(){
        val passShift = DBShift(shiftID, shift.start,shift.end,shift.breaks,shift.hours,shift.rate,shift.pay)
        try {
            db.deleteShift(passShift)
            Toast.makeText(this,"Deleted Shift : ${shift.start}", Toast.LENGTH_SHORT).show()
        } catch (e:Exception){
            Toast.makeText(this,"Error Deleting Shift : ${shift.start}", Toast.LENGTH_SHORT).show()
        }

        finish()
    }




}

