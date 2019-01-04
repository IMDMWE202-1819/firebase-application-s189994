package com.example.karina.firebaseapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.renderscript.ScriptIntrinsicBLAS.UNIT
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.karina.firebaseapp.R.layout.activity_uploading
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_uploading.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.IllegalStateException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storageRef = FirebaseStorage.getInstance().reference

    private var imageToUpload:Uri? = null
    private var imageDownload:String? = null

    private val markerMap = hashMapOf<String, String>()

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

        mMap.setOnMarkerClickListener { marker ->
            val id:String = markerMap[marker.id] as String

            db.collection("MapEvents")
                .document(id)
                .get()
                .addOnCompleteListener {task ->
                    val document = task.result
                    if ( document != null ) {
                        val eventTitle = document.get("title") as String
                        val eventDescription = document.get("description") as String
                        val eventImage:String? = document.get("image_url") as String?

                        val dialogView = layoutInflater.inflate(R.layout.marker_info, null)

                        val builder = AlertDialog.Builder(this)
                        builder.setView(dialogView).setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }.create().show()

                    }
            }
            false
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            fusedLocationClient.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    val location = it.result

                    if ( location != null ) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 12.0f))
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
                            val marker = mMap.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)))

                            markerMap[marker.id] = document.id
                        }
                    }
                }
            }
    }

    val PICK_IMAGE = 1234

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ( requestCode == PICK_IMAGE) {
            if ( data != null) {
                imageToUpload = data.data
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private suspend fun uploadImage() {
        GlobalScope.async {
            if (imageToUpload != null) {
                val imageUri = imageToUpload
                val email = mAuth.currentUser!!.email
                val currentDate = Calendar.getInstance().time
                val imageRef = storageRef.child("$email/$currentDate.png")
                if (imageUri != null) {
                 val task = imageRef.putFile(imageUri)

                    while ( task.isInProgress) {
                        delay(10)
                    }

                    if ( task.isSuccessful ) {
                        val meta = task.snapshot.metadata
                        val uploadRef = meta?.reference
                        if ( uploadRef != null ) {

                            val downloadUrl = uploadRef.downloadUrl
                            while(!downloadUrl.isComplete) {
                                delay(10)
                            }

                            if ( downloadUrl.isSuccessful)
                            {
                                imageDownload = downloadUrl.result.toString()
                            }
                        }
                    }
                }
            }
        }.await()
    }

    fun onFabClicked(view:View) {

        if (mAuth.currentUser == null) {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        } else {
          //  val intent = Intent(this, MainActivity::class.java)
          //  startActivity(intent)

           val dialogView = layoutInflater.inflate(R.layout.activity_uploading, null)
           val upload_button = dialogView.findViewById<Button>(R.id.btnChoose)

            upload_button.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
            }
           val builder = AlertDialog.Builder(this)
            builder.setView(dialogView)
                .setPositiveButton("Upload"
                ) { dialog, _ ->
                    val titleText = dialogView.findViewById<TextView>(R.id.dialog_title_text)
                    val descriptionText = dialogView.findViewById<TextView>(R.id.dialog_description_text)

                    val event = hashMapOf<String, Any?>()
                    event["title"] = titleText.text.toString()
                    event["description"] = descriptionText.text.toString()
                    event["user"] = mAuth.currentUser?.email

                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                    try {
                        fusedLocationClient.lastLocation.addOnCompleteListener {
                            if (it.isSuccessful) {
                                val location = it.result

                                if ( location != null ) {
                                    val currentLocation = GeoPoint(location.latitude, location.longitude)
                                    event["location"] = currentLocation

                                    runBlocking {
                                        uploadImage()
                                    }

                                    event["image_url"] = imageDownload

                                    db.collection("MapEvents").add(event).addOnCompleteListener { task ->
                                        if ( task.isSuccessful ) {
                                            val taskId = task.result!!.id
                                            val marker = mMap.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)))
                                            markerMap[marker.id] = taskId
                                        }
                                    }

                                }
                            }
                        }
                    }
                    catch (ex:SecurityException) {
                        Log.w("GEO", "security error", ex)
                    }


                }
                .setNegativeButton("Cancel"
                ) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create().show()
        }
    }}

   // private void chooseImage() {
     //   val btnChoose: Button
       // val btnUpload: Button
        //val imageView: ImageView
        //val filePath: Uri
        //Intent intent = new Intent();
        //btnChoose = (Button)findViewById(R.id.btnChoose);
       // btnUpload = (Button)findViewById(R.id.btnUpload);
       // imageView = (ImageView)findViewById(R.id.imgView);

 //       btnChoose.setOnClickListener(new View.onClickListener()) {
   //         @Override
     //       public void onClick (View.view) {
       //         chooseImage();
         //   }
        //}

      //  btnUpload.setOnClickListener(new View.onClickListener()) {
        //    @Override
          //  public void onClick (View view) {
//
  //          }
    //    })
//
  //  }
//}
