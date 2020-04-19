package com.flangenet.shiftlog.Controller

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.flangenet.shiftlog.Adapter.ListShiftsAdapter
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.DBHelper
import com.flangenet.shiftlog.Utilities.EXTRA_EDIT_SHIFT
import kotlinx.android.synthetic.main.activity_list_shifts.*
import java.io.File
import java.time.LocalDate
import java.time.temporal.ChronoField

class ListShifts : AppCompatActivity() {

    lateinit var  db: DBHelper
    var lstShifts: List<DBShift> = ArrayList<DBShift>()
    lateinit var shiftsAdapter: ListShiftsAdapter
    var wcDate: LocalDate = LocalDate.now()

    override fun onResume() {

        super.onResume()
        Toast.makeText(this,"I'm Resuming",Toast.LENGTH_SHORT).show()
        db = DBHelper(this)
        refreshData()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_shifts)
        //db = DBHelper(this)
        //refreshData()
        val currentDate = LocalDate.now()
        wcDate = currentDate.with(ChronoField.DAY_OF_WEEK,1)

        //var weDate = currentDate.with(ChronoField.DAY_OF_WEEK,7)
        println("Week Commencing Date : $wcDate")
        //println("Week Commencing Ending : $weDate")
        btnListLeft.setOnClickListener{btnListLeftClicked()}
        btnListRight.setOnClickListener{btnListRightClicked()}
        btnListDisplay.setOnClickListener{btnListDisplayClicked()}
    }






    fun refreshData(){

        btnListDisplay.text = wcDate.toString()

        lstShifts = db.getShifts(wcDate)

        shiftsAdapter = ListShiftsAdapter(this, lstShifts as ArrayList<DBShift>){shift ->
            Toast.makeText(this,"Do whatever needs to be done with shift ${shift.id}",Toast.LENGTH_SHORT).show()
            val editShiftIntent = Intent(this,EditShift::class.java)
            editShiftIntent.putExtra(EXTRA_EDIT_SHIFT, shift.id)
            startActivity(editShiftIntent)
        }
        listShiftsView.adapter = shiftsAdapter
        val layoutManager = LinearLayoutManager(this)
        listShiftsView.layoutManager = layoutManager
    }

    fun btnListLeftClicked(){
        wcDate = wcDate.minusWeeks(1)
        refreshData()


    }
    fun btnListRightClicked(){
        wcDate = wcDate.plusWeeks(1)
        refreshData()
    }

    fun btnListDisplayClicked(){

        var removableStoragePath: String = ""
        val fileList = File("/storage/").listFiles()
        for (file in fileList) {
            if (!file.absolutePath.equals(
                    Environment.getExternalStorageDirectory().absolutePath,
                    ignoreCase = true
                ) && file.isDirectory && file.canRead()
            ) removableStoragePath = file.absolutePath
        }
        //If there is an SD Card, removableStoragePath will have it's path. If there isn't it will be an empty string.

        println("SDCard : $removableStoragePath")


        //println("${getExternalFilesDir}")
                println("${Environment.DIRECTORY_DOWNLOADS}")
        //File("/${Environment.DIRECTORY_DOWNLOADS}/shifts.csv").forEachLine { println(it) }

    }



}
