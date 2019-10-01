package com.futsell.app.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.futsell.app.MainActivity
import com.futsell.app.R
import com.futsell.app.util.PrefsHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    //untuk request code
    private val RC_SIGN_IN = 7
    //untuk sign in client
    private lateinit var mGoogleSignIn: GoogleSignInClient
    //untuk firebase authentication
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        fAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignIn = GoogleSignIn.getClient(this, gso)
        sign_in_button.setOnClickListener {
            signIn()
        }

        btn_login.setOnClickListener {
            val nama = et_nama.text.toString()
            val password = et_psw.text.toString()

            if (nama.isNotEmpty() || password.isNotEmpty() ||
                !nama.equals("") || !password.equals("")
            ) {
                fAuth.signInWithEmailAndPassword(nama, password)
                    .addOnSuccessListener {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this, "LOGIN GAGAL",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(
                    this, "LOGIN GAGAL",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        tv_signup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }


    private fun signIn() {
        val signInIntent = mGoogleSignIn.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        d("FAUTH_LOGIN", "firebashAuth : ${account.id}")

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        fAuth.signInWithCredential(credential).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(
                    this,
                    "Login Berhasil Welcome ${fAuth.currentUser!!.displayName}",
                    Toast.LENGTH_SHORT
                ).show()
                val user = fAuth.currentUser
                updateUI(user)
            } else {
                Toast.makeText(
                    this,
                    "LOGIN GAGAL", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) = runWithPermissions(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) {
        if (user != null) {
            FirebaseDatabase.getInstance().getReference("user/${user.uid}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        PrefsHelper(this@LoginActivity).savePref(
                            p0.child("name").value.toString(),
                            PrefsHelper.FULLNAME
                        )
                        PrefsHelper(this@LoginActivity).savePref(
                            p0.child("email").value.toString(),
                            PrefsHelper.EMAIL
                        )
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))

                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.e("AUTH_LOGIN", "LOGIN_GAGAL", e)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = fAuth.currentUser
        if (user != null) {
            updateUI(user)
        }
    }

    override fun onResume() {
        super.onResume()
        val user = fAuth.currentUser
        if (user != null)
            updateUI(user)
    }

    override fun onPause() {
        super.onPause()
        val user = fAuth.currentUser
        if (user != null)
            finish()
    }

}




