package com.example.karina.firebaseapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login_page.*


class LoginPage : AppCompatActivity() {
    val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
    }

    fun loginOnClick(view: View) {
        // don't let the user try and login again if we've already made a login request to firebase
        if(activitySpinner.visibility == View.VISIBLE ) return

        val email = EmailText.text.toString()
        val password = PasswordText.text.toString()

        activitySpinner.visibility = View.VISIBLE

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            activitySpinner.visibility = View.INVISIBLE

            if(it.isSuccessful) {
                val intent= Intent(this,HomePage::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}




