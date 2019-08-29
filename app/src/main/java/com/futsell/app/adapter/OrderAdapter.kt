package com.futsell.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.futsell.app.R
import com.futsell.app.model.ModelClock

class OrderAdapter(val clocks : ArrayList<ModelClock>, val context : Context) : RecyclerView.Adapter<OrderAdapter.OrderHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        return OrderHolder(LayoutInflater.from(context).inflate(R.layout.item_order, parent, false))
    }

    override fun getItemCount(): Int {
        return clocks.size
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        val clock = clocks.get(position)
        holder.txtOClock.text = "${clock.clock}:00"
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            clock.checked = isChecked
        }
    }

    class OrderHolder(view : View) : RecyclerView.ViewHolder(view) {
        val txtOClock : TextView
        val checkBox : CheckBox

        init {
            txtOClock = view.findViewById(R.id.txtOClock)
            checkBox = view.findViewById(R.id.checkOrder)
        }
    }
}