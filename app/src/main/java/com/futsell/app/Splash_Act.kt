package com.futsell.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class Splash_Act: AppCompatActivity() {

    private val SPLASH_TIME_OUT :Long = 3000 // 3 second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_act)

    }
}