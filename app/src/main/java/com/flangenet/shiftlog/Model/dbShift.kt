package com.flangenet.shiftlog.Model

import android.database.sqlite.SQLiteOpenHelper
import java.sql.SQLData
import java.time.LocalDateTime

class DBShift {

    var id : Int = 0
    var start : LocalDateTime? = null
    var end : LocalDateTime? = null
    var breaks : Float? = 0F
    var hours : Float? = 0F
    var rate : Float? = 0F
    var pay : Float? = 0F

    constructor(){}

    constructor(id: Int, start: LocalDateTime, end: LocalDateTime, breaks: Float, hours: Float, rate: Float, pay: Float){

        this.id = id
        this.start = start
        this.end = end
        this.breaks = breaks
        this.hours = hours
        this.rate = rate
        this.pay = pay
    }




}
