package com.flangenet.shiftlog.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flangenet.shiftlog.Adapter.ListShiftsAdapter
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.DBHelper
import com.flangenet.shiftlog.Utilities.EXTRA_EDIT_SHIFT
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
        lstShifts = db.allShifts

        shiftsAdapter = ListShiftsAdapter(this, lstShifts as ArrayList<DBShift>){shift ->

            Toast.makeText(this,"Do whatever needs to be done with shift ${shift.id}",Toast.LENGTH_SHORT).show()
            val editShiftIntent = Intent(this,NewShift::class.java)
            editShiftIntent.putExtra(EXTRA_EDIT_SHIFT, shift.id)
            startActivity(editShiftIntent)


        }
        listShiftsView.adapter = shiftsAdapter
        val layoutManager = LinearLayoutManager(this)
        listShiftsView.layoutManager = layoutManager
    }
}
