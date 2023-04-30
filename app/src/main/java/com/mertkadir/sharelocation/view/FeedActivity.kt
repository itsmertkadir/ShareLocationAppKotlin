package com.mertkadir.sharelocation.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mertkadir.sharelocation.R
import com.mertkadir.sharelocation.databinding.ActivityFeedBinding
import com.mertkadir.sharelocation.model.Post
import org.json.JSONObject
import java.security.Permission
import java.util.prefs.Preferences

class FeedActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private  var  trackBoolean : Boolean? = null
    //private var selectedLatitude : Double? = null
    //private var selectedLongitude : Double? = null
    private lateinit var selectLatLng: LatLng
    private var placeFromMain : Post? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        auth = Firebase.auth
        firestore = Firebase.firestore

        registerLauncher()

        //selectedLatitude = 0.0
        //selectedLongitude = 0.0
        sharedPreferences = this.getSharedPreferences("com.mertkadir.sharelocation", MODE_PRIVATE)
        trackBoolean = false



    }


    override fun onMapReady(googleMap: GoogleMap) {
        if (googleMap != null){
            mMap = googleMap
        }

        val intent = intent
        val infoIntent = intent.getStringExtra("info")


        if (infoIntent == "old"){
            binding.button2.visibility = View.GONE
            mMap.clear()

            placeFromMain = intent.getSerializableExtra("locationLatLng") as? Post

            placeFromMain?.let {


                val jsonString = it.selectedLatLng
                val jsonObject = JSONObject(jsonString)

                val lat = jsonObject.getDouble("latitude")
                val lng = jsonObject.getDouble("longitude")

                val secLatLng = LatLng(lat,lng)



                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(secLatLng,15f))
                mMap.addMarker(MarkerOptions().position(secLatLng).title(it.locationName))
                binding.commentText.setText(it.comment)
                binding.locationNameText.setText(it.locationName)
            }

        }else{
            binding.button2.visibility = View.VISIBLE

            mMap.setOnMapLongClickListener(this)

            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    trackBoolean = sharedPreferences.getBoolean("trackBoolean",false)
                    if (trackBoolean == false) {
                        val userLocation = LatLng(location.latitude,location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                        sharedPreferences.edit().putBoolean("trackBoolean",true).apply()
                    }

                }
            }




            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)){

                    Snackbar.make(binding.root,"Permission Needed",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                        permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }.show()

                }else{
                    permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }

            }else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if (lastLocation != null ){
                    val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
                }
                mMap.isMyLocationEnabled = true
            }
        }









    }

    private fun registerLauncher(){

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if (lastLocation != null ){
                    val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
                }
                mMap.isMyLocationEnabled = true
            }

            if (result){

            }else{
                Toast.makeText(this, "Permission needed", Toast.LENGTH_SHORT).show()
            }


        }

    }



    fun shareBTN(view : View){



        val postMap = hashMapOf<String, Any>()
        postMap.put("locationName",binding.locationNameText.text.toString())
        postMap.put("locationLatLng",selectLatLng)
        postMap.put("comment",binding.commentText.text.toString())
        //postMap.put("userEmail",auth.currentUser!!.email!!)
        postMap.put("date",Timestamp.now())


        firestore.collection("Posts").add(postMap).addOnSuccessListener {

            finish()

        }.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }


    }

    override fun onMapLongClick(p0: LatLng) {

        mMap.clear()

        mMap.addMarker(MarkerOptions().position(p0))
        //selectedLatitude = p0.latitude
        //selectedLongitude = p0.longitude

       selectLatLng = p0




    }

}