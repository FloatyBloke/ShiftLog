package com.flangenet.shiftlog.Utilities

import com.flangenet.shiftlog.Controller.App
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun sqlToDatetime(sqlDate: String): LocalDateTime {
    return LocalDateTime.parse(sqlDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
}

fun datetimeToSQL(inDate: LocalDateTime) : String {
    return inDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
}

fun dateToSQLDate(inDate: LocalDate) : String {
    return inDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
fun sqlDateToDate(sqlDate:String) : LocalDate {
    return LocalDate.parse(sqlDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

fun prefsDateConvert(inDate: LocalDate) : String {
    //println(App.prefs.dateFormat)
    return inDate.format(DateTimeFormatter.ofPattern(App.prefs.dateFormat))
}
fun prefsTimeConvert(inTime: LocalTime?) : String{
    //println(App.prefs.timeFormat)
    return inTime!!.format(DateTimeFormatter.ofPattern(App.prefs.timeFormat))
}

fun removeBrackets(inputString: String) : String {
    var tempString= inputString.replace("[","",true)
    tempString= tempString.replace("]","",true)
    return tempString
}
fun properCase(inputString: String) : String {
    var outputString:String = ""
    inputString.forEach {
        if (outputString == ""){
            outputString += it.toUpperCase()
        } else {
            outputString += it.toLowerCase()
        }
    }
    return outputString
}