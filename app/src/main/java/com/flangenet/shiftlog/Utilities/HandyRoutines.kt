package com.flangenet.shiftlog.Utilities


import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.flangenet.shiftlog.Controller.App
import com.flangenet.shiftlog.Controller.MainActivity
import com.flangenet.shiftlog.Model.DBShift
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat.forPattern
import org.joda.time.format.DateTimeFormatter
import java.io.File
import java.io.FilenameFilter
import java.util.ArrayList


/*fun sqlToDatetime(sqlDate: String): LocalDateTime {
    return LocalDateTime.parse(sqlDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
}*/


fun sqlToDatetime(sqlDate: String): LocalDateTime {

    //return LocalDateTime.parse(sqlDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    val fmt = forPattern("yyyy-MM-dd HH:mm")
    return fmt.parseDateTime(sqlDate).toLocalDateTime()


}



/*fun datetimeToSQL(inDate: LocalDateTime) : String {
    return inDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))*/

fun datetimeToSQL(inDate: LocalDateTime) : String {
    val fmt:DateTimeFormatter = forPattern("yyyy-MM-dd HH:mm")
    return fmt.print(inDate)
}

/*fun dateToSQLDate(inDate: LocalDate) : String {
    return inDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}*/

fun dateToSQLDate(inDate: LocalDate) : String {
    val fmt:DateTimeFormatter = forPattern("yyyy-MM-dd")
    return fmt.print(inDate)
}

/*fun sqlDateToDate(sqlDate:String) : LocalDate {
    return LocalDate.parse(sqlDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}*/

fun sqlDateToDate(sqlDate:String) : LocalDate {
    val fmt = forPattern("yyyy-MM-dd")
    return fmt.parseDateTime(sqlDate).toLocalDate()
}

fun datetimeToFilename(inDate: LocalDateTime) : String {
    val fmt:DateTimeFormatter = forPattern("yyyyMMddHHmm")
    return fmt.print(inDate)
}

/*fun prefsDateConvert(inDate: LocalDate) : String {
    //println(App.prefs.dateFormat)
    return inDate.format(DateTimeFormatter.ofPattern(App.prefs.dateFormat))
}*/

fun makeDateTime(year:Int, month:Int, day:Int, hour:Int, minute:Int) : LocalDateTime {
    var outDateTime = LocalDateTime()
    outDateTime = outDateTime.withDate(year,month,day)
    outDateTime = outDateTime.withTime(hour, minute,0,0)
    return outDateTime
}
fun getDayOfWeek(inDate: LocalDate) : String {
    val fmt:DateTimeFormatter = forPattern("EEEE")
    return fmt.print(inDate)

}
fun prefsDateConvert(inDate: LocalDate) : String {

    val fmt:DateTimeFormatter = forPattern(App.prefs.dateFormat)
    return fmt.print(inDate)
}

/*fun prefsTimeConvert(inTime: LocalTime?) : String{
    //println(App.prefs.timeFormat)
    return inTime!!.format(DateTimeFormatter.ofPattern(App.prefs.timeFormat))
}*/

fun prefsTimeConvert(inTime: LocalTime?) : String{
    //println(App.prefs.timeFormat)
    val fmt:DateTimeFormatter = forPattern(App.prefs.timeFormat)
    return fmt.print(inTime)
}

fun removeBrackets(inputString: String) : String {
    var tempString= inputString.replace("[","",true)
    tempString= tempString.replace("]","",true)
    return tempString
}


fun properCase(inputString: String) : String {
    var outputString = ""
    inputString.forEach {
        if (outputString == ""){
            outputString += it.toUpperCase()
        } else {
            outputString += it.toLowerCase()
        }
    }
    return outputString
}

fun hideSoftKeyBoard(context: Context, view: View) {
    try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    } catch (e: Exception) {

        e.printStackTrace()
    }

}
fun backupSQLiteDatabase(context: Context){

    val backups =  mutableListOf<String>()

    var db = DBHelper(context)
    //var outFN = File(context.getExternalFilesDir(null),"/${datetimeToFilename(LocalDateTime())}${SQLITE_DATABASE_NAME}")
    var outFN = File(context.getExternalFilesDir(null),"/${SQLITE_DATABASE_NAME}")
    println("SQLite Backup - {${outFN.absoluteFile}")
    try{
        File("${db.getPath()}").copyTo(outFN, true)
    } catch (e: Exception){
        println("SQLite Backup Error")
    }

    /*
    File(context.getExternalFilesDir(null),"/").walkTopDown().forEach { it ->

        var t = it.absoluteFile.toString()
        println("BACKUPS : $it")
        if (t.endsWith("ShiftLog.db", true)) {
            backups.add("$it")
        }
    }

    backups.reverse()
    var bCount = backups.count()
    println("Number of backups : $bCount")

    while (bCount > 5){
        println("${bCount-1} - ${backups[bCount-1]}")
        File(backups[bCount-1]).delete()
        bCount--
    }
    */

}




