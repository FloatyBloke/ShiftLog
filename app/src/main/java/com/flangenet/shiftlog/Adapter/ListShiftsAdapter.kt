package com.flangenet.shiftlog.Adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flangenet.shiftlog.Model.DBShift
import com.flangenet.shiftlog.R
import kotlinx.android.synthetic.main.row_layout.view.*

class ListShiftsAdapter (val context:Context, val shifts: ArrayList<DBShift>): RecyclerView.Adapter<ListShiftsAdapter.ViewHolder>(){
        inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
                val id = itemView?.findViewById<TextView>(R.id.gridId)
                val start = itemView?.findViewById<TextView>(R.id.gridStart)
                val end = itemView?.findViewById<TextView>(R.id.gridEnd)
                val breaks = itemView?.findViewById<TextView>(R.id.gridBreak)
                val hours =itemView?.findViewById<TextView>(R.id.gridHours)
                val rate =itemView?.findViewById<TextView>(R.id.gridRate)
                val pay =itemView?.findViewById<TextView>(R.id.gridPay)

                fun bindMessage(context: Context, shift:DBShift) {
                        id?.text = shift.id.toString()
                        start?.text = shift.start.toString()
                        end?.text = shift.end.toString()
                        breaks?.text = shift.breaks.toString()
                        hours?.text = shift.hours.toString()
                        rate?.text = shift.rate.toString()
                        pay?.text = shift.pay.toString()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(context).inflate(
                        R.layout.row_layout,parent,false)
                return ViewHolder(view)
        }

        override fun getItemCount(): Int {
                return shifts.count()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder?.bindMessage(context,shifts[position])
        }
}
