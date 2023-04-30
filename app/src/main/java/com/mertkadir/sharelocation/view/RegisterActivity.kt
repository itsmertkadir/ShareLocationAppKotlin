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
import com.mertkadir.sharelocation.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth

    private lateinit var email : String
    private lateinit var password : String
    private lateinit var passwordVerify : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Init
        auth = Firebase.auth


        val currentUser = auth.currentUser

        if (currentUser != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun logIn(view : View) {

        val intentToLogin = Intent(this,LoginActivity::class.java)
        startActivity(intentToLogin)
        finish()

    }

    fun signIn(view: View) {

        email = binding.emailText.text.toString()
        password = binding.passwordText.text.toString()
        passwordVerify = binding.passwordVerificationText.text.toString()

        if (email.equals("") || password.equals("") || passwordVerify.equals("")){
            Toast.makeText(this, "Enter Email and Password, Password Verify", Toast.LENGTH_LONG).show()
        }else if (password != passwordVerify) {
            Toast.makeText(this, "Password do not match", Toast.LENGTH_LONG).show()
        }else{

           auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
               val intent = Intent(this,MainActivity::class.java)
               startActivity(intent)
               finish()
           }.addOnFailureListener {
               Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
           }

        }

    }
}