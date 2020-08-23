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
import com.flangenet.shiftlog.Utilities.getDayOfWeek
import com.flangenet.shiftlog.Utilities.prefsDateConvert
import com.flangenet.shiftlog.Utilities.prefsTimeConvert


class ListShiftsAdapter (val context:Context, val shifts: ArrayList<DBShift>, val itemClick: (DBShift) -> Unit): RecyclerView.Adapter<ListShiftsAdapter.ViewHolder>(){

        class ViewHolder (itemView: View, val itemClick: (DBShift) -> Unit) : RecyclerView.ViewHolder(itemView){
                private val id: TextView = itemView.findViewById(R.id.gridId)
                private val start: TextView = itemView.findViewById(R.id.gridStart)
                private val end: TextView = itemView.findViewById(R.id.gridEnd)
                private val breaks: TextView = itemView.findViewById(R.id.gridBreak)

                val hours: TextView = itemView.findViewById(R.id.gridHours)
                //val rate = itemView.findViewById<TextView>(R.id.gridRate)
                val pay: TextView = itemView.findViewById(R.id.gridPay)
                val tips: TextView = itemView.findViewById(R.id.gridTips)

                fun bindShifts(shift:DBShift) {
                        //id?.text = "${shift.id} - ${shift.start!!.dayOfWeek}"
                        //id.text = properCase(shift.start!!.dayOfWeek.toString())
                        id.text = getDayOfWeek(shift.start!!.toLocalDate())
                        start.text = "${shift.start}"
                        start.text = prefsDateConvert(shift.start!!.toLocalDate())
                        val t = "${prefsTimeConvert(shift.start?.toLocalTime())} - ${prefsTimeConvert(shift.end?.toLocalTime())}"
                        end.text = t
                        breaks.text = String.format("%.2f",shift.breaks)
                        hours.text = String.format("%.2f",shift.hours)
                        //rate?.text = shift.rate.toString()
                        pay.text = String.format("%.2f",shift.pay)
                        tips.text = String.format("%.2f",shift.tips)
                        if (!App.prefs.viewTipsColumn!!) {
                                tips.visibility = View.GONE
                        }
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
                holder.bindShifts(shifts[position])
        }


}


