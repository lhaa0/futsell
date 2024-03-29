package com.futsell.app.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.futsell.app.R
import com.futsell.app.util.ChatMessage
import com.futsell.app.util.SettingApi

class ChatDetailListAdapter(private val mContext: Context, private val mMessages: MutableList<ChatMessage>, val uid : String) : BaseAdapter() {
    internal var set: SettingApi

    init {
        set = SettingApi(mContext)
    }

    override fun getCount(): Int {
        return mMessages.size
    }

    override fun getItem(position: Int): Any {
        return mMessages[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convert = convertView
        val msg = getItem(position) as ChatMessage
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder()
            convert = LayoutInflater.from(mContext).inflate(R.layout.row_chat_details, parent, false)
            holder.time = convert!!.findViewById<View>(R.id.text_time) as TextView
            holder.message = convert.findViewById<View>(R.id.text_content) as TextView
            holder.lyt_thread = convert.findViewById<View>(R.id.lyt_thread) as CardView
            holder.lyt_parent = convert.findViewById<View>(R.id.lyt_parent) as LinearLayout
            holder.image_status = convert.findViewById<View>(R.id.image_status) as ImageView
            convert.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        holder.message!!.text = msg.text
        holder.time!!.text = msg.readableTime

        if (msg.receiver.id == uid) {
            holder.lyt_parent!!.setPadding(15, 10, 100, 10)
            holder.lyt_parent!!.gravity = Gravity.LEFT
            holder.lyt_thread!!.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.image_status!!.setImageResource(android.R.color.transparent)
        } else {
            holder.lyt_parent!!.setPadding(100, 10, 15, 10)
            holder.lyt_parent!!.gravity = Gravity.RIGHT
            holder.lyt_thread!!.setCardBackgroundColor(mContext.resources.getColor(R.color.secondary))
            holder.image_status!!.setImageResource(R.drawable.baseline_done_24)
            holder.image_status!!.setColorFilter(ContextCompat.getColor(mContext, android.R.color.darker_gray), android.graphics.PorterDuff.Mode.MULTIPLY)

            if (msg.isRead!!) {
                holder.image_status!!.setImageResource(R.drawable.baseline_done_all_24)
                holder.image_status!!.setColorFilter(ContextCompat.getColor(mContext, android.R.color.holo_blue_dark), android.graphics.PorterDuff.Mode.MULTIPLY)
            }
        }
        return convert!!
    }

    /**
     * remove data item from messageAdapter
     */
    fun remove(position: Int) {
        mMessages.removeAt(position)
    }

    /**
     * add data item to messageAdapter
     */
    fun add(msg: ChatMessage) {
        mMessages.add(msg)
    }

    private class ViewHolder {
        internal var time: TextView? = null
        internal var message: TextView? = null
        internal var lyt_parent: LinearLayout? = null
        internal var lyt_thread: CardView? = null
        internal var image_status: ImageView? = null
    }
}