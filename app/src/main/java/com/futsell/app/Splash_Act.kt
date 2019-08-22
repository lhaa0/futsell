package com.futsell.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity

class Splash_Act: AppCompatActivity() {

    private val SPLASH_TIME_OUT :Long = 3000 // 3 second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_act)

        Handler().postDelayed({

            startActivity(Intent(this@Splash_Act, MainActivity::class.java))

            finish()
        }, SPLASH_TIME_OUT)
    }
    }
