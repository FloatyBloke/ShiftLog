package com.flangenet.shiftlog.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flangenet.shiftlog.Controller.App
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.dateConvert
import com.flangenet.shiftlog.Utilities.timeConvert
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ListShiftsAdapter (val context:Context, val shifts: ArrayList<DBShift>, val itemClick: (DBShift) -> Unit): RecyclerView.Adapter<ListShiftsAdapter.ViewHolder>(){

        inner class ViewHolder (itemView: View, val itemClick: (DBShift) -> Unit) : RecyclerView.ViewHolder(itemView){
                val id = itemView.findViewById<TextView>(R.id.gridId)
                val start = itemView.findViewById<TextView>(R.id.gridStart)
                val end = itemView.findViewById<TextView>(R.id.gridEnd)
                val breaks = itemView.findViewById<TextView>(R.id.gridBreak)
                val hours = itemView.findViewById<TextView>(R.id.gridHours)
                val rate = itemView.findViewById<TextView>(R.id.gridRate)
                val pay = itemView.findViewById<TextView>(R.id.gridPay)

                fun bindShifts(context: Context, shift:DBShift) {
                        //id?.text = "${shift.id} - ${shift.start!!.dayOfWeek}"
                        id?.text = (shift.start!!.dayOfWeek.toString()).toLowerCase().capitalize()
                        start?.text = "${shift.start}"
                        start.text = dateConvert(shift.start!!.toLocalDate())

                        end?.text = "${timeConvert(shift.start?.toLocalTime())} - ${timeConvert(shift.end?.toLocalTime())}"

                        breaks?.text = String.format("%.2f",shift.breaks)
                        hours?.text = String.format("%.2f",shift.hours)
                        rate?.text = shift.rate.toString()
                        pay?.text = String.format("%.2f",shift.pay)
                        itemView.setOnClickListener{itemClick(shift)
                        }
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(context).inflate(
                        R.layout.row_layout,parent,false)
                return ViewHolder(view,itemClick)
        }

        override fun getItemCount(): Int {
                return shifts.count()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.bindShifts(context,shifts[position])
        }


}


