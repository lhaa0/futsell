package com.futsell.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.futsell.app.MainActivity
import com.futsell.app.R
import com.futsell.app.model.ModelFutsal

class MainAdapter(val futsals: ArrayList<ModelFutsal>, val context: Context) :
    RecyclerView.Adapter<MainAdapter.MainHolder>() {

    val onClickItem : onItemClickListener

    init {
        onClickItem = context as MainActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return MainHolder(v)
    }

    override fun getItemCount(): Int {
        return futsals.size
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val futsal = futsals.get(position)
        val no = position + 1
        holder.nameFutsal.text = "$no. ${futsal.nama_futsal}"
        holder.addressfutsal.text = futsal.alamat_futsal
        holder.layItem.setOnClickListener {
            onClickItem.onClick(futsal)
        }
    }

    class MainHolder(view : View) : RecyclerView.ViewHolder(view) {

        val nameFutsal : TextView
        val addressfutsal : TextView
        val layItem : LinearLayout

        init {
            nameFutsal = view.findViewById(R.id.nameFutsal)
            addressfutsal = view.findViewById(R.id.addressFutsal)
            layItem = view.findViewById(R.id.layItem)
        }
    }

    interface onItemClickListener{
        fun onClick(futsal: ModelFutsal)
    }
}