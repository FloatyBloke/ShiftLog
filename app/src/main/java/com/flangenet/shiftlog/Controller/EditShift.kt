package com.flangenet.shiftlog.Controller

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.Model.Shift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_edit_shift.*
import org.joda.time.DateTimeZone

import org.joda.time.LocalDateTime
import org.joda.time.Minutes


//import java.time.*
//import java.time.format.DateTimeFormatter




class EditShift : AppCompatActivity() {
    private lateinit var db: DBHelper
    private var shiftID: Int = 0

    var shift = Shift(LocalDateTime(),LocalDateTime(),0F,0F,5F,0F)

    lateinit var mAdView : AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_shift)

        shiftID = intent.getIntExtra(EXTRA_EDIT_SHIFT,0)
        if (shiftID > 0) { txtNewShiftTitle.text = getString(R.string.edit_shift)}
        btnShiftSave.setOnClickListener{btnSaveShiftClicked()}
        btnShiftDelete.setOnClickListener{btnShiftDeleteClicked()}

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

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
                override fun onItemSelected(parent: AdapterView<*>,view: View?, position: Int, id: Long) {
                    //Toast.makeText(this@NewShift,breakArray[position], Toast.LENGTH_SHORT).show()
                    val breakMins: Int = spinner.selectedItem.toString().toInt()
                    shift.breaks = (breakMins.toFloat()/60)
                    //println(shift.breaks)
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
            var tTime = LocalDateTime()
            //tTime = tTime.minusSeconds(tTime.second.toLong())
            //tTime = tTime.minusNanos(tTime.nano.toLong())
            tTime = tTime.minusSeconds(tTime.secondOfMinute)
            tTime = tTime.minusMillis(tTime.millisOfSecond)
            shift.start= tTime
            shift.end = tTime
            shift.breaks = 0F
            shift.rate = App.prefs.hourlyRate
            btnShiftDelete.visibility = View.INVISIBLE
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

        // Button click to show DatePickerDialog
        txtShiftStartDate.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, outYear, outMonth, outDay ->
                shift.start = makeDateTime(outYear,(outMonth+1),outDay, shift.start.hourOfDay,shift.start.minuteOfHour)
                mainCalc()
            }, shift.start.year, ((shift.start.monthOfYear)-1), shift.start.dayOfMonth)
            dpd.show()
        }

        txtShiftEndDate.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, outYear, outMonth, outDay ->
                shift.end = makeDateTime(outYear,(outMonth+1),outDay,shift.end.hourOfDay,shift.end.minuteOfHour)
                mainCalc()
            }, shift.end.year, ((shift.end.monthOfYear)-1), shift.end.dayOfMonth)
            dpd.show()
        }

        txtShiftStartTime.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, outHour, outMinute ->
                shift.start = makeDateTime(shift.start.year,shift.start.monthOfYear,shift.start.dayOfMonth,outHour,outMinute)
                mainCalc()
            }
            TimePickerDialog(this, timeSetListener, shift.start.hourOfDay, shift.start.minuteOfHour, true).show()
        }

        txtShiftEndTime.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, outHour, outMinute ->
                shift.end = makeDateTime(shift.end.year,shift.end.monthOfYear,shift.end.dayOfMonth,outHour,outMinute)
                mainCalc()
            }
            TimePickerDialog(this, timeSetListener, shift.end.hourOfDay, shift.end.minuteOfHour, true).show()
        }
        mainCalc()
    }

    fun mainCalc() {
        // Calculations to update information

        //val shiftMins = Duration.between(shift.start,shift.end).toMinutes()

        val calcMins = Minutes.minutesBetween(shift.start, shift.end)

        val shiftMins: Int = calcMins.minutes

        val shiftHours: Float = (shiftMins.toFloat()/60)
        val breakHours: Float = shift.breaks
        val shiftHoursTotal = shiftHours - breakHours
        val hourlyRate: Float = shift.rate
        val shiftPay: Float = shiftHoursTotal * hourlyRate

        shift.hours = shiftHoursTotal
        shift.pay = shiftPay
        //shift.breaks = breakHours

        // Update screen
/*        txtShiftStartDate.text = shift.start.format(DateTimeFormatter.ofPattern(App.prefs.dateFormat))
        txtShiftEndDate.text = shift.end.format(DateTimeFormatter.ofPattern(App.prefs.dateFormat))
        txtShiftStartTime.text = shift.start.format(DateTimeFormatter.ofPattern(App.prefs.timeFormat))
        txtShiftEndTime.text = shift.end.format(DateTimeFormatter.ofPattern(App.prefs.timeFormat))*/
        txtShiftStartDate.text = prefsDateConvert(shift.start.toLocalDate())
        txtShiftEndDate.text = prefsDateConvert(shift.end.toLocalDate())
        txtShiftStartTime.text = prefsTimeConvert(shift.start.toLocalTime())
        txtShiftEndTime.text = prefsTimeConvert(shift.end.toLocalTime())
        txtHours.text = shift.hours.toString()
        txtShiftHourlyRate.text = String.format("%.2f",shift.rate)
        txtPay.text = String.format("%.2f",shift.pay)

        if (shift.hours < 0F) {
            txtHours.background = getDrawable(R.drawable.wak_shadow_error)
            Toast.makeText(this,"Shifts cannot have negative values", Toast.LENGTH_LONG).show()
            btnShiftSave.isEnabled = false
        } else {
            txtHours.background = getDrawable(R.drawable.wak_shadow)
            btnShiftSave.isEnabled = true
        }

    }

    private fun btnSaveShiftClicked(){
        if (shiftID == 0){
            try {
                val passShift = DBShift(1, shift.start,shift.end,shift.breaks,shift.hours,shift.rate,shift.pay)
                db.addShift(passShift)
            } catch (e:Exception){
                Toast.makeText(this,"${getString(R.string.error_creating_shift)} : ${prefsDateConvert(shift.start.toLocalDate())}", Toast.LENGTH_SHORT).show()
            }

        } else {
            try {
                val passShift = DBShift(shiftID, shift.start,shift.end,shift.breaks,shift.hours,shift.rate,shift.pay)
                db.updateShift(passShift)
            } catch (e:Exception){
                Toast.makeText(this,"${getString(R.string.error_updating_shift)} : ${prefsDateConvert(shift.start.toLocalDate())}", Toast.LENGTH_SHORT).show()
            }

        }
        finish()
    }


    private fun btnShiftDeleteClicked(){
        val passShift = DBShift(shiftID, shift.start,shift.end,shift.breaks,shift.hours,shift.rate,shift.pay)
        try {
            db.deleteShift(passShift)
            Toast.makeText(this,"${getString(R.string.shift_deleted)} : ${prefsDateConvert(shift.start.toLocalDate())}", Toast.LENGTH_SHORT).show()
        } catch (e:Exception){
            Toast.makeText(this,"${getString(R.string.error_deleting_shift)} : ${prefsDateConvert(shift.start.toLocalDate())}", Toast.LENGTH_SHORT).show()
        }
        finish()
    }




}

