package com.example.acropolis

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.acropolis.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        mAuth=FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            binding.signincardview.visibility=View.GONE
            binding.signupcardview.visibility=View.VISIBLE
        }

        binding.btnsigninfromsignup.setOnClickListener {
            binding.signincardview.visibility=View.VISIBLE
            binding.signupcardview.visibility=View.GONE
        }
        binding.btnvalidatefromsignin.setOnClickListener {
            val email=binding.SigninEmail.text.toString()
            val password=binding.SigninPassword.text.toString()
            login(email,password)
        }
        binding.btnvalidatefromsignup.setOnClickListener {
            val email = binding.SignupEmail.text.toString()
            val password = binding.SignupPassword.text.toString()
            val userName=binding.SignUpUsername.text.toString()

            signup(email, password,userName)
        }
    }
    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
            if (task.isSuccessful){
                if (mAuth.currentUser!!.isEmailVerified){
                    val intent = Intent(this, HomeScreen::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this,"please verify your email",Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this,"user doesn't exist",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun signup(email: String,password: String,username:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener { task->
                      if (task.isSuccessful){
                          addUserToDatabase(email,username,mAuth.currentUser!!.uid)
                          binding.signincardview.visibility=View.VISIBLE
                          binding.signupcardview.visibility=View.GONE
                          Toast.makeText(this, "Verification link sent to your Email", Toast.LENGTH_SHORT).show()
                      }
                    }

                } else {
                    Toast.makeText(this, "else executed", Toast.LENGTH_SHORT).show()
                }

            }

    }
    private fun addUserToDatabase(email:String,name: String, uid: String) {
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase.child("user").child(uid).setValue(dataofperson(email,name))
    }
}

data class dataofperson(
    val email: String,
    val name: String
)