package com.flangenet.shiftlog.Model

import java.time.LocalDateTime

class Shift(var start: LocalDateTime, var end: LocalDateTime, var breaks: Float, var hours:Float, var rate: Float, var pay: Float)
