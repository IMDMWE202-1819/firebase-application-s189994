package com.example.karina.firebaseapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home_page.*

class HomePage : AppCompatActivity() {

    val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        //val mapView = ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 16)

        if (mAuth.currentUser == null) {
            fab_addContent.hide()
        } else {
            fab_addContent.show()
        }
        // if (mAuth.currentUser == null) {
         //   Login_Button.hide()
       // } else {
      //      Login_Button.hide()
       // }
        fun onLoginClicked(view: View) {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
    }
}
