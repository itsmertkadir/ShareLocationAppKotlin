package com.mertkadir.sharelocation.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mertkadir.sharelocation.R
import com.mertkadir.sharelocation.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth

    private lateinit var email : String
    private lateinit var password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Init
        auth = Firebase.auth
    }

    fun logIn(view : View){

        email = binding.emailTextLogin.text.toString()
        password = binding.passwordTextLogin.text.toString()

        if (email.equals("") || password.equals("")){
            Toast.makeText(this, "Enter Email and Password", Toast.LENGTH_LONG).show()
        }else {
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {

                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun signIn(view : View){

        val intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

}