package com.flangenet.shiftlog.Model

import org.joda.time.LocalDateTime

//import java.time.LocalDateTime

class Shift(var start: LocalDateTime, var end: LocalDateTime, var breaks: Float, var hours:Float, var rate: Float, var pay: Float)
