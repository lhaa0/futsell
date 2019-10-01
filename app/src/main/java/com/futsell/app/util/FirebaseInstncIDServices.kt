package com.futsell.app.util

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseInstncIDServices : FirebaseMessagingService(){
    private val FRIENDLY_ENGAGE_TOPIC = "friendly_engage"

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        FirebaseMessaging.getInstance().subscribeToTopic(FRIENDLY_ENGAGE_TOPIC)
        Log.d("NEW_TOKEN", s)
    }

}