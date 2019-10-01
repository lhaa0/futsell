package com.futsell.app.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.futsell.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    var value = 0.0
    lateinit var fAuth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        fAuth = FirebaseAuth.getInstance()

        tv_signin.setOnClickListener {
            finish()
        }

        btn_register.setOnClickListener {
            val name = et_nama.text.toString()
            val email = et_email.text.toString()
            val password = et_psw.text.toString()
            if (name.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()){
                if (password == et_psw2.text.toString()) {
                    fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                simpanToFirebase(name, email, password)
                                Toast.makeText(this, "Register Berhasil!", Toast.LENGTH_SHORT)
                                    .show()
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Password must be 6 or more digit!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "There's some empty input!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun simpanToFirebase(name: String, email: String, password: String) {
        val uidUser = fAuth.currentUser?.uid
        dbRef = FirebaseDatabase.getInstance().getReference("user/$uidUser")
        dbRef.child("/id").setValue(uidUser)
        dbRef.child("/name").setValue(name)
        dbRef.child("/email").setValue(email)
        dbRef.push()
//        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
