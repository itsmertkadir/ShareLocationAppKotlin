package com.mertkadir.sharelocation.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import com.mertkadir.sharelocation.adapter.RecyclerAdapter
import com.mertkadir.sharelocation.databinding.ActivityMainBinding
import com.mertkadir.sharelocation.model.Post

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var recyclerAdapter : RecyclerAdapter
    private lateinit var selectedString : String
    private lateinit var latLngLocation : LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Init
        auth = Firebase.auth
        db = Firebase.firestore

        postArrayList = ArrayList<Post>()

        getData()

        binding.recylerView.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = RecyclerAdapter(postArrayList)
        binding.recylerView.adapter = recyclerAdapter
    }

    private fun getData(){

        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->

            if(error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }else{

                if (value != null) {
                    if (!value.documents.isNullOrEmpty()){

                        println(value.size())

                        val documents = value.documents

                        postArrayList.clear()


                        for (document in documents) {

                            val post = Post(
                                document.get("comment") as String,
                                document.get("locationName") as String,
                                document.get("date") as Timestamp,
                                LatLng((document.get("latLang") as HashMap<String,Double>)["latitude"]!!, (document.get("latLang") as HashMap<String,Double>)["longitude"]!!)
                            )
                            postArrayList.add(post)


                        }

/*
                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val locationName = document.get("locationName") as String
                            //val userEmail = document.get("userEmail") as String
                            val selectedLatLng = document.get("locationLatLng")
                            val post = Post(comment,locationName, selectedLatLng)
                            postArrayList.add(post)
                            }

 */


                        recyclerAdapter.notifyDataSetChanged()

                    }
                }

            }

        }



    }

    fun logOut(view : View){
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }
    fun goToFeed(view: View){

        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtra("info","new")
        startActivity(intent)

    }

}