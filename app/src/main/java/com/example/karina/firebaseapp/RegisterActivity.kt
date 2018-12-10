package com.example.karina.firebaseapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.karina.firebaseapp.R.string.password
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

    }

    fun RegisterClick(view:View) {

        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        activityBar.visibility = View.VISIBLE

        val passwordConfirm = passwordConfirmText.text.toString()
       val email = emailText.text.toString()
        val password = passwordText.text.toString()

        if (password != passwordConfirm) {
            Toast.makeText(baseContext, "Passwords must match",
                Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {

                Log.d("AUTH", "createUserWithEmail:success")
                val user = mAuth.currentUser

                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)

            } else {
                // If sign in fails, display a message to the user.
                Log.w("AUTH", "createUserWithEmail:failure", it.exception)
                Toast.makeText(this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
            }

        }
    }
}


