package com.example.acropolis

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_home_screen.*

class HomeScreen : AppCompatActivity() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        mAuth= FirebaseAuth.getInstance()
        mDatabase=FirebaseDatabase.getInstance().getReference()
        mDatabase.child("user").child(mAuth.currentUser!!.uid).get().addOnSuccessListener {
            val snap= it.getValue(data::class.java)!!
            userName.text=snap.name
        }.addOnFailureListener{
            Log.i("firebase", "Error getting data", it)
        }


    }
}
data class data(
    val email: String?=null,
    val name: String?=null
)