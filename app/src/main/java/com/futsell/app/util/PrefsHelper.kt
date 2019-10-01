package com.futsell.app.util

import android.content.Context
import android.content.SharedPreferences

class PrefsHelper(ctx: Context) {

    companion object {
        val USER_ID = "uidx"
        val COUNTER_ID = "counter_id"
        val FULLNAME = "fullname"
        val EMAIL = "email"
    }

    val mContext : Context = ctx
    val sharedSet : SharedPreferences

    init {
        sharedSet = mContext.getSharedPreferences("FUTSELLAPP", Context.MODE_PRIVATE)
    }

    fun savePref(value : String, pref : String) {
        val edit = sharedSet.edit()
        edit.putString(pref, value)
        edit.apply()
    }

    fun getPref(pref : String) : String?{
        return sharedSet.getString(pref, "")
    }
}