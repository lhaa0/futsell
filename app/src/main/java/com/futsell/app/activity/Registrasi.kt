package com.futsell.app.activity

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.futsell.app.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.register_act.*

class Registrasi : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_act)

        btnRegis.setOnClickListener {
            val nama = etNama.text.toString()
            val email = etEmail.text.toString()
            val pass = etPsw.text.toString()
            handlingForRegister(nama, email, pass)
        }
    }

    private fun handlingForRegister(uname: String, email: String, pass: String) {
        when {
            uname == null -> ("username kosong")
            email == null -> ("email kosong")
            pass == null -> ("password kosong")
//            else -> alert (title = "REGISTRASI", message = "User Berhasil Registrasi"){
//                positiveButton(buttonText = "OK"){
//                    onBackPressed()
//                    finish()
//                }
//                isCancelable = false
//            }.show()

        }
    }
}

//    lateinit var registerBtn: Button
//    lateinit var regEmail: EditText
//    lateinit var regPassword: EditText
//    lateinit var regUsername: EditText
//    lateinit var mAuth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.register_act)
//
//        mAuth = FirebaseAuth.getInstance()
//
//        regEmail = findViewById(R.id.etEmail)
//        regPassword = findViewById(R.id.etPsw)
//        regUsername = findViewById(R.id.etNama)
//        registerBtn = findViewById(R.id.btnRegis)
//
//
//        val name = regUsername.text.toString()
//        val psw = regPassword.text.toString()
//        val email = regEmail.text.toString()
//
//        if (TextUtils.isEmpty(name)) {
//
//            regUsername.error = "Enter Name"
//            return
//        }
//        if (TextUtils.isEmpty(email)) {
//
//            regUsername.error = "Enter Email"
//            return
//        }
//        if (TextUtils.isEmpty(psw)) {
//
//            regUsername.error = "Enter Password"
//            return
//        }
//        CreateUser(name, email, psw)
//
//    }
//
//    private fun CreateUser(name: String, email : String, psw : String) {
//
//        mAuth.createUserWithEmailAndPassword(email, psw)
//            .addOnCompleteListener (this){ task ->
//                if (task.isSuccessful) {
//
//                    val currenUser = FirebaseAuth.getInstance().currentUser
//                    val uid = currenUser!!.uid
//
//                    val userMap = HashMap<String, String>()
////                    userMap{"name"} = name
//                }
//            }





