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

fun dateConvert(inDate: LocalDate) : String {
    //println(App.prefs.dateFormat)
    return inDate.format(DateTimeFormatter.ofPattern(App.prefs.dateFormat))
}
fun timeConvert(inTime: LocalTime?) : String{
    //println(App.prefs.timeFormat)
    return inTime!!.format(DateTimeFormatter.ofPattern(App.prefs.timeFormat))
}