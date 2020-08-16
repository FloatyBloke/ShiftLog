package com.flangenet.shiftlog.Controller

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.Model.Shift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_edit_shift.*

import org.joda.time.LocalDateTime
import org.joda.time.Minutes
import java.io.File
import java.nio.file.Files.copy


//import java.time.*
//import java.time.format.DateTimeFormatter




class EditShift : AppCompatActivity() {
    private lateinit var db: DBHelper
    private var shiftID: Int = 0

    var shift = Shift(LocalDateTime(),LocalDateTime(),0F,0F,5F,0F, 0F)
    var inShift = Shift(LocalDateTime(),LocalDateTime(),0F,0F,5F,0F, 0F)
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
            shift.tips = 0F
            btnShiftDelete.visibility = View.INVISIBLE
        } else {
            val tShift:DBShift = db.getShift(shiftID)
            shift.start = tShift.start!!
            shift.end = tShift.end!!
            shift.breaks = tShift.breaks!!
            shift.hours = tShift.hours!!
            shift.rate = tShift.rate!!
            shift.tips = tShift.tips!!
            shift.pay = tShift.pay!!
        }

        // Store initial values
        inShift.start = shift.start
        inShift.end = shift.end
        inShift.breaks = shift.breaks
        inShift.hours = shift.hours
        inShift.rate = shift.rate
        inShift.tips = shift.tips
        inShift.pay = shift.pay




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
        // Set up only field that has keyboard entry
        edtTips.setText(shift.tips.toString())
        edtTips.setOnClickListener{
            edtTips.selectAll()
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

        hideKeyboard()

    }

    private fun btnSaveShiftClicked(){


        // Backup before save internal SQLite Database backup

        backupSQLiteDatabase(this)

        shift.tips = edtTips.text.toString().toFloat()

        if (shiftID == 0){
            try {
                val passShift = DBShift(1, shift.start,shift.end,shift.breaks,shift.hours,shift.rate,shift.pay,shift.tips)
                db.addShift(passShift)
            } catch (e:Exception){
                Toast.makeText(this,"${getString(R.string.error_creating_shift)} : ${prefsDateConvert(shift.start.toLocalDate())}", Toast.LENGTH_SHORT).show()
            }

        } else {
            try {
                val passShift = DBShift(shiftID, shift.start,shift.end,shift.breaks,shift.hours,shift.rate,shift.pay,shift.tips)
                db.updateShift(passShift)
            } catch (e:Exception){
                Toast.makeText(this,"${getString(R.string.error_updating_shift)} : ${prefsDateConvert(shift.start.toLocalDate())}", Toast.LENGTH_SHORT).show()
            }

        }
        finish()
    }


    private fun btnShiftDeleteClicked(){

        shift.tips = edtTips.text.toString().toFloat()

        val passShift = DBShift(shiftID, shift.start,shift.end,shift.breaks,shift.hours,shift.rate,shift.pay,shift.tips)
        try {
            db.deleteShift(passShift)
            Toast.makeText(this,"${getString(R.string.shift_deleted)} : ${prefsDateConvert(shift.start.toLocalDate())}", Toast.LENGTH_SHORT).show()
        } catch (e:Exception){
            Toast.makeText(this,"${getString(R.string.error_deleting_shift)} : ${prefsDateConvert(shift.start.toLocalDate())}", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun openTipsDlg() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.tips_dialog)
        val tips= dialog.findViewById<TextView>(R.id.txtDlgTips)

        tips.text = "${shift.tips}"

        //tips.setOnClickListener{ dialog.dismiss() }
        val btnTipsOk = dialog.findViewById(R.id.btnTipsOk) as Button

        btnTipsOk.setOnClickListener {
            edtTips.text = tips.editableText
            dialog.dismiss()
        }


        dialog.show()

    }

    private fun hideKeyboard(){
        val inputManager  = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            // Back button pressed , check if info has changed and if you ask to save
            //println("Back button pressed")
            //Toast.makeText( this,"Back button pressed", Toast.LENGTH_SHORT).show()
            //updateStockCheck()
            if (infoChanged()){
                showSaveDialog()
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    private fun showSaveDialog() {
        lateinit var dialog: AlertDialog

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Save Shift ?")
        builder.setMessage("This shift has been altered, would you like to save it ?")
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {

                        btnSaveShiftClicked()
                        finish()

                }
                DialogInterface.BUTTON_NEGATIVE -> finish()
            }
        }
        builder.setPositiveButton("YES",dialogClickListener)
        builder.setNegativeButton("NO",dialogClickListener)
        dialog = builder.create()
        dialog.show()

    }

    private fun toast(message : String){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()

    }


    private fun infoChanged() : Boolean {
        mainCalc()
        shift.tips = edtTips.text.toString().toFloat()
        var changed: Boolean = false
        println("${inShift.start}-${shift.start}" )
        println("${inShift.end}-${shift.end}" )
        println("${inShift.breaks}-${shift.breaks}" )
        println("${inShift.hours}-${shift.hours}" )
        println("${inShift.rate}-${shift.rate}" )
        println("${inShift.pay}-${shift.pay}")
        println("${inShift.tips}-${shift.tips}")
        if (inShift.start != shift.start || inShift.end != shift.end || inShift.breaks != shift.breaks || inShift.hours != shift.hours || inShift.rate != shift.rate || inShift.pay != shift.pay || inShift.tips != shift.tips){
            changed = true
        }
        //println(changed)
        return changed
    }

}

