package com.futsell.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.profil_act.*

class Profil: AppCompatActivity() {

    lateinit var fAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profil_act)

        fAuth = FirebaseAuth.getInstance()
        btn_logout.setOnClickListener {
            fAuth.signOut()
            onBackPressed()
        }
    }
}