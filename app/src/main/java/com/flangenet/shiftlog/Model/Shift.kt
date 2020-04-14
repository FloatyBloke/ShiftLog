package com.flangenet.shiftlog.Model

import java.time.LocalDateTime

class Shift(val start: LocalDateTime, val end: LocalDateTime, val breaks: Float, val hours:Int, val rate: Float, val pay: Float)
