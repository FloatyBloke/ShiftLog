package com.flangenet.shiftlog.Controller

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.flangenet.shiftlog.Adapter.ListShiftsAdapter
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_list_shifts.*
import org.joda.time.LocalDate
//import java.time.LocalDate
//import java.time.temporal.ChronoField
import kotlin.math.abs

//private const val DEBUG_TAG = "Gestures"

class ListShifts : AppCompatActivity(), GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    lateinit var db: DBHelper
    var lstShifts: List<DBShift> = ArrayList()
    lateinit var shiftsAdapter: ListShiftsAdapter
    lateinit var wcDate: LocalDate
    var searchMode: Int = 0

    //private var oldNoisy: SoundPool? = null
    //private val soundId = 1
    private var noisy: MediaPlayer? = null

    var gDetector: GestureDetectorCompat? = null

    lateinit var mAdView : AdView





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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_shifts)

        //oldNoisy = SoundPool(6, AudioManager.STREAM_MUSIC, 0)

        //oldNoisy?.load(baseContext, R.raw.netswish, 1)
        noisy = MediaPlayer.create(applicationContext, R.raw.netswish)

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        this.gDetector = GestureDetectorCompat(this,this)
        gDetector?.setOnDoubleTapListener(this)

        val currentDate = LocalDate()

        if (savedInstanceState != null){
            wcDate = sqlDateToDate(savedInstanceState.getString(WEEK_COMMENCING_DATE)!!)
        } else {
            //wcDate = currentDate.withDate(ChronoField.DAY_OF_WEEK, (App.prefs.weekStartDay.toLong()) + 1)
            wcDate = currentDate.minusDays(currentDate.dayOfWeek - (App.prefs.weekStartDay) - 1)
        }



        btnListLeft.setOnClickListener { btnListLeftClicked() }
        btnListRight.setOnClickListener { btnListRightClicked() }



/*
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

*/
        listShiftsView.setOnTouchListener(OnTouchListener { v, event ->

            this.gDetector?.onTouchEvent(event)
            false
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





    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.gDetector?.onTouchEvent(event)
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event)
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
        itemTouchHelper.attachToRecyclerView(listShiftsView)
        */


        var totalBreaks = 0F
        var totalHours = 0F
        var totalPay = 0F
        var totalTips = 0F


        lstShifts.forEach { shift ->
            totalBreaks += shift.breaks!!
            totalHours += shift.hours!!
            totalPay += shift.pay!!
            totalTips += shift.tips!!
        }

        txtTotalBreaks.text = String.format("%.2f", totalBreaks)
        txtTotalHours.text = String.format("%.2f", totalHours)
        txtTotalPay.text = String.format("%.2f", totalPay)
        txtTotalTips.text = String.format("%.2f", totalTips)

        when (searchMode) {
            0 -> { val t = getString(R.string.week) + " " + prefsDateConvert(wcDate)
                txtWeekCommencing.text = t }
            1 -> { val t = "${properCase(wcDate.toString("MMMM"))} ${wcDate.year}"
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


    override fun onShowPress(e: MotionEvent?) {
        //txtWeekCommencing.text = "Press"
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        //txtWeekCommencing.text = "onSingleTapUp"
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        //txtWeekCommencing.text = "onDown"
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val SWIPE_THRESHOLD = 100
        val SWIPE_VELOCITY_THRESHOLD = 100
        val result = false
        try {
            val diffY = e2!!.y - e1!!.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        btnListLeftClicked()
                    } else {
                        btnListRightClicked()
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }





        //txtWeekCommencing.text = "Swipe"
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        //txtWeekCommencing.text = "onScroll"
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        //txtWeekCommencing.text = "onLongPress"
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        //txtWeekCommencing.text = "onDoubleTap"
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        //txtWeekCommencing.text = "onDoubleTapEvent"
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        //txtWeekCommencing.text = "onSingleTapConfirmed"
        return true
    }
}

