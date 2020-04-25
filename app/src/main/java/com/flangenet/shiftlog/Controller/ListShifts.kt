package com.flangenet.shiftlog.Controller

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.flangenet.shiftlog.Adapter.ListShiftsAdapter
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.DBHelper
import com.flangenet.shiftlog.Utilities.EXTRA_EDIT_SHIFT
import com.flangenet.shiftlog.Utilities.dateConvert
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_list_shifts.*
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.util.*
import kotlin.collections.ArrayList

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
        wcDate = currentDate.with(ChronoField.DAY_OF_WEEK,(App.prefs.weekStartDay.toLong())+1)
        //println("Week Commencing Date : $wcDate")
        //println("Week Commencing Ending : $weDate")

        btnListLeft.setOnClickListener{btnListLeftClicked()}
        btnListRight.setOnClickListener{btnListRightClicked()}

        findViewById<View>(R.id.listShiftsView).setOnTouchListener{v,event ->
            println("Touchy : ${event}")
            true

        }

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
            val editShiftIntent = Intent(this,EditShift::class.java)
            editShiftIntent.putExtra(EXTRA_EDIT_SHIFT, shift.id)
            startActivity(editShiftIntent)
        }

        listShiftsView.adapter = shiftsAdapter
        val layoutManager = LinearLayoutManager(this)
        listShiftsView.layoutManager = layoutManager
        var totalBreaks = 0F
        var totalHours = 0F
        var totalPay = 0F

        lstShifts.forEach{shift ->
            totalBreaks += shift.breaks!!
            totalHours += shift.hours!!
            totalPay += shift.pay!!
        }

        txtTotalBreaks.text = "${String.format("%.2f",totalBreaks)} "
        txtTotalHours.text = "${String.format("%.2f",totalHours)} "
        txtTotalPay.text = "${String.format("%.2f", totalPay)} "

        when(searchMode){
            0 -> txtWeekCommencing.text = "${getString(R.string.week)} ${dateConvert(wcDate)}"
            1 -> txtWeekCommencing.text = "${((wcDate.month.toString())).toLowerCase(Locale.ROOT).capitalize()} ${wcDate.year}"
            2 -> txtWeekCommencing.text = "${getString(R.string.year)} ${wcDate.year}"
        }

        if(lstShifts.count() ==0){
            txtNoShifts.visibility = View.VISIBLE
        } else {
            txtNoShifts.visibility = View.INVISIBLE
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

}
