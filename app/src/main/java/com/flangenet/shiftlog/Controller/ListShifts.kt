package com.flangenet.shiftlog.Controller

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.flangenet.shiftlog.Adapter.ListShiftsAdapter
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_list_shifts.*
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.util.*
import kotlin.collections.ArrayList

//private const val DEBUG_TAG = "Gestures"

class ListShifts : AppCompatActivity() {

    lateinit var db: DBHelper
    var lstShifts: List<DBShift> = ArrayList<DBShift>()
    lateinit var shiftsAdapter: ListShiftsAdapter
    lateinit var wcDate: LocalDate
    var searchMode: Int = 0

    //private var oldNoisy: SoundPool? = null
    //private val soundId = 1
    private var noisy: MediaPlayer? = null




    companion object {
        private val TAG = ListShifts::class.java.simpleName
        private const val WEEK_COMMENCING_DATE = "WEEK_COMMENCING_DATE"
    }

    override fun onResume() {
        super.onResume()
        db = DBHelper(this)
        refreshData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(WEEK_COMMENCING_DATE, dateToSQLDate(wcDate))

    }

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_shifts)

        //oldNoisy = SoundPool(6, AudioManager.STREAM_MUSIC, 0)

        //oldNoisy?.load(baseContext, R.raw.netswish, 1)
        noisy = MediaPlayer.create(applicationContext, R.raw.netswish)

        val currentDate = LocalDate.now()

        if (savedInstanceState != null){
            wcDate = sqlDateToDate(savedInstanceState.getString(WEEK_COMMENCING_DATE)!!)
        } else {
            wcDate = currentDate.with(ChronoField.DAY_OF_WEEK, (App.prefs.weekStartDay.toLong()) + 1)
        }



        btnListLeft.setOnClickListener { btnListLeftClicked() }
        btnListRight.setOnClickListener { btnListRightClicked() }


        // set up right and left swipe to call buttons
        listShiftsView.setOnTouchListener(object : OnSwipeTouchListener() {
            override fun onSwipeLeft() {
                //Log.e("ViewSwipe", "Left")
                btnListRightClicked()
            }

            override fun onSwipeRight() {
                //Log.e("ViewSwipe", "Right")
                btnListLeftClicked()
            }
        })


        tabMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 1
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 2
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                //println(tab!!.position)
                if (tab != null) {
                    searchMode = tab.position
                    refreshData()
                }
            }
        })
    }





    fun refreshData() {

        lstShifts = db.getShifts(wcDate, searchMode)

        shiftsAdapter = ListShiftsAdapter(this, lstShifts as ArrayList<DBShift>) { shift ->
            val editShiftIntent = Intent(this, EditShift::class.java)
            editShiftIntent.putExtra(EXTRA_EDIT_SHIFT, shift.id)
            startActivity(editShiftIntent)
        }

        listShiftsView.adapter = shiftsAdapter
        val layoutManager = LinearLayoutManager(this)
        listShiftsView.layoutManager = layoutManager

/*        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                println("Swiped : $direction")
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(listShiftsView)*/


        var totalBreaks = 0F
        var totalHours = 0F
        var totalPay = 0F


        lstShifts.forEach { shift ->
            totalBreaks += shift.breaks!!
            totalHours += shift.hours!!
            totalPay += shift.pay!!
        }

        txtTotalBreaks.text = String.format("%.2f", totalBreaks)
        txtTotalHours.text = String.format("%.2f", totalHours)
        txtTotalPay.text = String.format("%.2f", totalPay)

        when (searchMode) {
            0 -> { val t = getString(R.string.week) + " " + prefsDateConvert(wcDate)
                txtWeekCommencing.text = t }
            1 -> { val t = "${properCase(wcDate.month.toString())} ${wcDate.year}"
                txtWeekCommencing.text = t }
            2 -> {val t = "${getString(R.string.year)} ${wcDate.year}"
                txtWeekCommencing.text = t}
        }

        if (lstShifts.count() == 0) {
            txtNoShifts.visibility = View.VISIBLE
        } else {
            txtNoShifts.visibility = View.INVISIBLE
        }

    }

    fun btnListLeftClicked() {
        when (searchMode) {
            0 -> wcDate = wcDate.minusWeeks(1)
            1 -> wcDate = wcDate.minusMonths(1)
            2 -> wcDate = wcDate.minusYears(1)

        }
        //oldNoisy?.play(soundId, 1F, 1F, 0, 0, 1F)
        noisy?.start()
        refreshData()
    }

    fun btnListRightClicked() {
        when (searchMode) {
            0 -> wcDate = wcDate.plusWeeks(1)
            1 -> wcDate = wcDate.plusMonths(1)
            2 -> wcDate = wcDate.plusYears(1)

        }
        //oldNoisy?.play(soundId, 1F, 1F, 0, 0, 1F)
        noisy?.start()
        refreshData()
    }
}

