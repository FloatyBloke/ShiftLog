package com.flangenet.shiftlog.Utilities

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.flangenet.shiftlog.Model.DBShift
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class DBHelper(context: Context) :SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER) {

    companion object {
        private val DATABASE_VER = 1
        private val DATABASE_NAME = "ShiftLog.db"

        // Table
        private val TABLE_NAME = "ShiftList"
        private val COL_ID = "Id"
        private val COL_START = "Start"
        private val COL_END = "End"
        private val COL_BREAKS = "Breaks"
        private val COL_HOURS = "Hours"
        private val COL_RATE = "Rate"
        private val COL_PAY = "Pay"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY_STRING = ("CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_START TEXT, $COL_END TEXT, $COL_BREAKS REAL, $COL_HOURS REAL, $COL_RATE REAL, $COL_PAY REAL)")
        println(CREATE_TABLE_QUERY_STRING)
        db!!.execSQL(CREATE_TABLE_QUERY_STRING)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)

    }

    //CRUD
    val allShifts:List<DBShift>
        get() {
            val lstShifts = ArrayList<DBShift>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery,null)

            if (cursor.moveToFirst()) {
                do {
                    val tshift = DBShift()

                    tshift.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                    tshift.start = sqlToDatetime(cursor.getString(cursor.getColumnIndex(COL_START)))
                    //println("*${cursor.getString(cursor.getColumnIndex(COL_START))}*")
                    //println(sqlToDatetime(cursor.getString(cursor.getColumnIndex(COL_START))))
                        //
                    tshift.end = sqlToDatetime(cursor.getString(cursor.getColumnIndex(COL_END)))
                    tshift.breaks = cursor.getFloat(cursor.getColumnIndex(COL_BREAKS))
                    tshift.hours = cursor.getFloat(cursor.getColumnIndex((COL_HOURS)))
                    tshift.rate = cursor.getFloat(cursor.getColumnIndex((COL_RATE)))
                    tshift.pay = cursor.getFloat(cursor.getColumnIndex((COL_PAY)))

                    lstShifts.add(tshift)
                } while (cursor.moveToNext())
            }
            return lstShifts
        }

    fun getShift(shiftID:Int): DBShift {

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_ID=$shiftID"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery,null)

        val tShift:DBShift = DBShift()

        if (cursor.moveToFirst()) {
                tShift.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                tShift.start = sqlToDatetime(cursor.getString(cursor.getColumnIndex(COL_START)))

                tShift.end = sqlToDatetime(cursor.getString(cursor.getColumnIndex(COL_END)))
                tShift.breaks = cursor.getFloat(cursor.getColumnIndex(COL_BREAKS))
                tShift.hours = cursor.getFloat(cursor.getColumnIndex((COL_HOURS)))
                tShift.rate = cursor.getFloat(cursor.getColumnIndex((COL_RATE)))
                tShift.pay = cursor.getFloat(cursor.getColumnIndex((COL_PAY)))
        }

        return tShift
        }


    fun addShift(shift: DBShift){
        val db=  this.writableDatabase
        val values = ContentValues()
        //values.put(COL_ID, person.id) as it's an auto number
        values.put(COL_START,datetimeToSQL(shift.start!!))
        values.put(COL_END,datetimeToSQL(shift.end!!))
        values.put(COL_BREAKS,shift.breaks)
        values.put(COL_HOURS,shift.hours)
        values.put(COL_RATE, shift.rate)
        values.put(COL_PAY,shift.pay)
        println("*****************$values")
        db.insert(TABLE_NAME,null,values)
        db.close()

    }

    fun updateShift(shift: DBShift) : Int
    {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID, shift.id)
        values.put(COL_START,datetimeToSQL(shift.start!!))
        values.put(COL_END,datetimeToSQL(shift.end!!))
        values.put(COL_BREAKS,shift.breaks)
        values.put(COL_HOURS,shift.hours)
        values.put(COL_RATE, shift.rate)
        values.put(COL_PAY,shift.pay)


        return db.update(TABLE_NAME, values,"$COL_ID=?", arrayOf(shift.id.toString()))
    }

    fun deleteShift(shift: DBShift)
    {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(shift.id.toString()))
        db.close()
    }


     fun sqlToDatetime(sqlDate: String): LocalDateTime{
        // Convert SQL String date to LocalDateTime

        // SQL TEXT DATE as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS").
        // In : 2007-12-03 10:15:30.999
        // LocalDateTime
        //var t2:Formatter = "yyyy-MM-dd'T'HH:mm:ss'.'"
        //var t:LocalDateTime = LocalDateTime.parse(sqlDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'.'"))



        return LocalDateTime.parse(sqlDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }
    fun datetimeToSQL(inDate: LocalDateTime) : String {
        return inDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }
}