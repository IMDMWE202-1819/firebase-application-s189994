package com.example.karina.firebaseapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.renderscript.ScriptIntrinsicBLAS.UNIT
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    //button show different layouts
    //listener
    //dialog
    //
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            fusedLocationClient.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    val location = it.result

                    if ( location != null ) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 13.0f))
                    }
                }
            }
        }
        catch (ex:SecurityException) {
            Log.w("GEO", "security error", ex)
        }

        db.collection("MapEvents")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result
                    if (result != null) {
                        val documents = result.documents

                        for (document in documents) {
                            val location: GeoPoint = document.get("location") as GeoPoint
                            mMap.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)))
                        }
                    }
                }
            }
    }

    fun onFabClicked(view:View) {
        if ( mAuth.currentUser == null ){
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
        else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
