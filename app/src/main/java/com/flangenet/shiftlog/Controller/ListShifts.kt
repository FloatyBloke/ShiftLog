package com.flangenet.shiftlog.Controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flangenet.shiftlog.Adapter.ListShiftsAdapter
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.DBHelper
import kotlinx.android.synthetic.main.activity_list_shifts.*

class ListShifts : AppCompatActivity() {

    internal  lateinit var  db: DBHelper
    internal var lstShifts: List<DBShift> = ArrayList<DBShift>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_shifts)
        db = DBHelper(this)
        refreshData()
    }



    lateinit var shiftsAdapter: ListShiftsAdapter


    fun refreshData(){
        println("*********** AT start of Routine")
        lstShifts = db.allShifts
        println("Hello : ${lstShifts[1].hours}")

        shiftsAdapter = ListShiftsAdapter(this, lstShifts as ArrayList<DBShift>){shift ->
            println(shift.id)
            Toast.makeText(this,"Do whatever needs to be done with shift ${shift.id}",Toast.LENGTH_LONG).show()
        }
        listShiftsView.adapter = shiftsAdapter
        val layoutManager = LinearLayoutManager(this)
        listShiftsView.layoutManager = layoutManager
    }
}
