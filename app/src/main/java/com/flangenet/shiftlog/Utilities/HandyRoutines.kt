package com.flangenet.shiftlog.Utilities


import com.flangenet.shiftlog.Controller.App
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormat.forPattern
import org.joda.time.format.DateTimeFormatter


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