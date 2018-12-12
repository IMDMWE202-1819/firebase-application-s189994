package com.example.karina.firebaseapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    /*mAuth.getInstance().currentUser == null
    {
        val intent = Intent(this, LoginPage::class.java)
        startActivity(intent)
        button.title = "Sign out"
    }*/
}
