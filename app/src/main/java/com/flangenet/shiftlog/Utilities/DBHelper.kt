package com.flangenet.shiftlog.Utilities

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.flangenet.shiftlog.Controller.MainActivity
import com.flangenet.shiftlog.Model.DBShift
import org.joda.time.LocalDate
//import java.time.LocalDate
import kotlin.collections.ArrayList

class DBHelper(context: Context) :SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER) {

    companion object {
        private val DATABASE_VER = 2
        private val DATABASE_NAME = SQLITE_DATABASE_NAME

        // Table
        private val TABLE_NAME = "ShiftList"
        private val COL_ID = "Id"
        private val COL_START = "Start"
        private val COL_END = "End"
        private val COL_BREAKS = "Breaks"
        private val COL_HOURS = "Hours"
        private val COL_RATE = "Rate"
        private val COL_PAY = "Pay"
        private val COL_TIPS = "Tips"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY_STRING =
            ("CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_START TEXT, $COL_END TEXT, $COL_BREAKS REAL, $COL_HOURS REAL, $COL_RATE REAL, $COL_PAY REAL, $COL_TIPS REAL)")

            db!!.execSQL(CREATE_TABLE_QUERY_STRING)
        //db.close()

    }



    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        //onCreate(db)
        //Toast.makeText(MainActivity.applicationContext(),"Tips Exist : " , Toast.LENGTH_LONG).show()

        if(oldVersion == 1 && newVersion == 2){
            //println("Hello..... is this version 1 to 2 ? $oldVersion - $newVersion")
            //Log.d("UPG","Hello..... is this version 1 to 2 ?")

            try{
                val QUERY_STRING_1to2 =
                    ("ALTER TABLE $TABLE_NAME ADD COLUMN $COL_TIPS REAL")
                db!!.execSQL(QUERY_STRING_1to2)
                } catch (e: Exception) {
                // Do nothing
                //Log.e("DBUPG", "Impossible error")
                Toast.makeText(MainActivity.instance ,"Error converting Database",Toast.LENGTH_LONG).show()

            }


        }


    }

    fun getShifts(firstDate: LocalDate, searchMode: Int): List<DBShift> {



        val lstShifts = ArrayList<DBShift>()
        val searchDate = dateToSQLDate(firstDate)
        var selectQuery = "SELECT * FROM $TABLE_NAME"

        when (searchMode){

            0 -> selectQuery = " $selectQuery WHERE DATE(start) >= '$searchDate' AND DATE(start) <= DATE('$searchDate','+6 days')"
            1 -> selectQuery = " $selectQuery WHERE DATE(start) >= DATE('$searchDate','start of month') AND DATE(start) <= DATE('$searchDate','start of month','+1 month','-1 day')"
            2 -> selectQuery = " $selectQuery WHERE DATE(start) >= DATE('$searchDate','start of year') AND DATE(start) <= DATE('$searchDate','start of year', '+12 month','-1 day')"

        }

            selectQuery = " $selectQuery ORDER BY start"
            // println(selectQuery)

            val db = this.writableDatabase

        lateinit var cursor: Cursor

            try {

                cursor = db.rawQuery(selectQuery, null)

                if (cursor.moveToFirst()) {
                    do {
                        val tshift = DBShift()

                        tshift.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                        tshift.start =
                            sqlToDatetime(cursor.getString(cursor.getColumnIndex(COL_START)))
                        tshift.end = sqlToDatetime(cursor.getString(cursor.getColumnIndex(COL_END)))
                        tshift.breaks = cursor.getFloat(cursor.getColumnIndex(COL_BREAKS))
                        tshift.hours = cursor.getFloat(cursor.getColumnIndex((COL_HOURS)))
                        tshift.rate = cursor.getFloat(cursor.getColumnIndex((COL_RATE)))
                        tshift.pay = cursor.getFloat(cursor.getColumnIndex((COL_PAY)))
                        tshift.tips = cursor.getFloat(cursor.getColumnIndex(COL_TIPS))

                        lstShifts.add(tshift)
                    } while (cursor.moveToNext())
                }


            } catch (e: SQLiteException) {
                //Toast.makeText(EditShift(), "Database query error", Toast.LENGTH_SHORT).show()
            } finally {
                cursor.close()
                db.close()
            }



            return lstShifts

        }



    fun getShift(shiftID:Int): DBShift {

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_ID=$shiftID"
        val db = this.writableDatabase
        lateinit var cursor: Cursor
        val tShift = DBShift()

        try {
            cursor = db.rawQuery(selectQuery,null)


            if (cursor.moveToFirst()) {
                tShift.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                tShift.start = sqlToDatetime(cursor.getString(cursor.getColumnIndex(COL_START)))

                tShift.end = sqlToDatetime(cursor.getString(cursor.getColumnIndex(COL_END)))
                tShift.breaks = cursor.getFloat(cursor.getColumnIndex(COL_BREAKS))
                tShift.hours = cursor.getFloat(cursor.getColumnIndex((COL_HOURS)))
                tShift.rate = cursor.getFloat(cursor.getColumnIndex((COL_RATE)))
                tShift.pay = cursor.getFloat(cursor.getColumnIndex((COL_PAY)))
                tShift.tips = cursor.getFloat(cursor.getColumnIndex(COL_TIPS))
            }
        } catch (e: Exception){
            println(e.message)
        } finally {
            cursor.close()
            db.close()
        }



        return tShift
        }


    fun addShift(shift: DBShift){
        val db=  this.writableDatabase

        try{
            val values = ContentValues()
            //values.put(COL_ID, person.id) as it's an auto number
            values.put(COL_START,datetimeToSQL(shift.start!!))
            values.put(COL_END,datetimeToSQL(shift.end!!))
            values.put(COL_BREAKS,shift.breaks)
            values.put(COL_HOURS,shift.hours)
            values.put(COL_RATE, shift.rate)
            values.put(COL_PAY,shift.pay)
            values.put(COL_TIPS,shift.tips)
            db.insert(TABLE_NAME,null,values)
        } catch (e: Exception) {
            println(e.message)
        } finally {
            db.close()
        }

    }

    fun deleteTable(){
        val db = this.writableDatabase
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    fun emptyTable(context: Context){
        val db = this.writableDatabase
        db!!.execSQL("DELETE FROM  $TABLE_NAME")
        Toast.makeText(context,"Delete !!!", Toast.LENGTH_LONG).show()
        db.close()
    }

    fun createTable() {
        val db = this.writableDatabase
        onCreate(db)
    }

    fun getPath() : String  {
        val db = this.writableDatabase
        println("DB Path : ${db!!.path}")
        db.close()
        return db.path
    }

    fun updateShift(shift: DBShift) : Int {
        var fig = 0
        val db = this.writableDatabase
        try{

            val values = ContentValues()
            values.put(COL_ID, shift.id)
            values.put(COL_START,datetimeToSQL(shift.start!!))
            values.put(COL_END,datetimeToSQL(shift.end!!))
            values.put(COL_BREAKS,shift.breaks)
            values.put(COL_HOURS,shift.hours)
            values.put(COL_RATE, shift.rate)
            values.put(COL_PAY,shift.pay)
            values.put(COL_TIPS,shift.tips)
            fig = db.update(TABLE_NAME, values,"$COL_ID=?", arrayOf(shift.id.toString()))
        } catch (e: java.lang.Exception){
            println(e.message)
        } finally {
            db.close()
        }


        return fig
    }

    fun deleteShift(shift: DBShift) {

        val db = this.writableDatabase
        try {
            db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(shift.id.toString()))
        } catch (e:Exception){
            println(e.message)
        } finally {
            db.close()
        }


    }


     fun importShifts(lineList: MutableList<String>) : Int {

         val db=  this.writableDatabase
         val values = ContentValues()
         lateinit var importLine: List<String>
         var errorCount = 0

         try {
             lineList.forEach {
                 val rawLine = removeBrackets(it)
                 importLine = rawLine.split(",")
                 if (rawLine != "") {
                     values.put(COL_START, importLine[0])
                     values.put(COL_END, importLine[1])
                     values.put(COL_BREAKS, importLine[2].toFloat())
                     values.put(COL_HOURS, importLine[3].toFloat())
                     values.put(COL_RATE, importLine[4].toFloat())
                     values.put(COL_PAY, importLine[5].toFloat())
                     values.put(COL_TIPS, importLine[6].toFloat())
                     var t = db.insert(TABLE_NAME, null, values)
                     println("Importing : $t - $rawLine")
                 }
             }


         } catch (e: Exception) {
             Log.e("Import", e.message)
             errorCount += 1
             println("Parse Error : ${errorCount}*${importLine[0]}*${importLine[1]}*${importLine[2]}*${importLine[3]}*${importLine[4]}*${importLine[5]}*${importLine[6]}")

         } finally {
             db.close()

         }

         println("Import Complete : Errors : $errorCount")




        return errorCount
    }

    fun isColumnExists(table: String, column: String): Boolean {
        var isExists = false
        var cursor: Cursor? = null
        val db=  this.writableDatabase
        try {
            cursor = db.rawQuery("PRAGMA table_info($table)", null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val name: String = cursor.getString(cursor.getColumnIndex("name"))
                    if (column.equals(name, ignoreCase = true)) {
                        isExists = true
                        break
                    }
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed) cursor.close()
        }
        db.close()
        return isExists
    }



}