package com.flangenet.shiftlog.Controller

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.flangenet.shiftlog.Adapter.ListShiftsAdapter
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.Model.Shift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.DBHelper
import com.flangenet.shiftlog.Utilities.EXTRA_EDIT_SHIFT
import com.flangenet.shiftlog.Utilities.dateConvert
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_list_shifts.*
import java.io.File
import java.time.LocalDate
import java.time.temporal.ChronoField

class ListShifts : AppCompatActivity() {

    lateinit var  db: DBHelper
    var lstShifts: List<DBShift> = ArrayList<DBShift>()
    lateinit var shiftsAdapter: ListShiftsAdapter
    var wcDate: LocalDate = LocalDate.now()
    var searchMode: Int = 0

    override fun onResume() {

        super.onResume()
        //Toast.makeText(this,"I'm Resuming",Toast.LENGTH_SHORT).show()
        db = DBHelper(this)
        refreshData()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_shifts)

        val currentDate = LocalDate.now()
        wcDate = currentDate.with(ChronoField.DAY_OF_WEEK,1)
        println("Week Commencing Date : $wcDate")
        //println("Week Commencing Ending : $weDate")

        btnListLeft.setOnClickListener{btnListLeftClicked()}
        btnListRight.setOnClickListener{btnListRightClicked()}
        tabMode.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab:TabLayout.Tab?){
                // 1
            }
            override fun onTabUnselected(tab: TabLayout.Tab?){
                // 2
            }
            override fun onTabSelected(tab: TabLayout.Tab?){
                //println(tab!!.position)
                if (tab != null) {
                    searchMode = tab.position
                    refreshData()
                }

        }
        })
    }


    fun refreshData(){

        //btnListDisplay.text = wcDate.toString()

        lstShifts = db.getShifts(wcDate,searchMode)

        shiftsAdapter = ListShiftsAdapter(this, lstShifts as ArrayList<DBShift>){shift ->
            //Toast.makeText(this,"Do whatever needs to be done with shift ${shift.id}",Toast.LENGTH_SHORT).show()
            val editShiftIntent = Intent(this,EditShift::class.java)
            editShiftIntent.putExtra(EXTRA_EDIT_SHIFT, shift.id)
            startActivity(editShiftIntent)
        }

        listShiftsView.adapter = shiftsAdapter
        val layoutManager = LinearLayoutManager(this)
        listShiftsView.layoutManager = layoutManager
        //var totals: Shift? = null
        var totalBreaks:Float = 0F
        var totalHours: Float = 0F
        var totalPay: Float = 0F

        lstShifts.forEach{shift ->
            totalBreaks += shift.breaks!!
            totalHours += shift.hours!!
            totalPay += shift.pay!!
        }

        txtTotalBreaks.text = String.format("%.2f",totalBreaks)
        txtTotalHours.text = String.format("%.2f",totalHours)
        txtTotalPay.text = String.format("%.2f",totalPay)

        when(searchMode){
            0 -> txtWeekCommencing.text = "Week ${dateConvert(wcDate)}"
            1 -> txtWeekCommencing.text = "${wcDate.month.toString()} ${wcDate.year.toString()}"
            2 -> txtWeekCommencing.text = "${wcDate.year.toString()}"
        }

    }

    fun btnListLeftClicked(){
        when(searchMode){
            0 -> wcDate = wcDate.minusWeeks(1)
            1 -> wcDate = wcDate.minusMonths(1)
            2 -> wcDate = wcDate.minusYears(1)
        }

        refreshData()
    }

    fun btnListRightClicked(){
        when(searchMode) {
            0 -> wcDate = wcDate.plusWeeks(1)
            1 -> wcDate = wcDate.plusMonths(1)
            2 -> wcDate = wcDate.plusYears(1)
        }
        refreshData()
    }

    fun tabWeekClicked(){
        txtWeekCommencing.text = "Week"

    }

    fun tabMonthClicked(){
        txtWeekCommencing.text = "Month"

    }

    fun tabYearClicked(){
        txtWeekCommencing.text = "Year"

    }


}
