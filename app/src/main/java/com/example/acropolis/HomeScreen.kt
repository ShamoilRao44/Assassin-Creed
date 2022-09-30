package com.example.acropolis

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.acropolis.databinding.ActivityHomeScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_home_screen.*


class HomeScreen : AppCompatActivity() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    lateinit var binding : ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mAuth= FirebaseAuth.getInstance()
        mDatabase=FirebaseDatabase.getInstance().getReference()
        mDatabase.child("user").child(mAuth.currentUser!!.uid).get().addOnSuccessListener {
            val snap= it.getValue(data::class.java)!!
            userName.text=snap.name
        }.addOnFailureListener{
            Log.i("firebase", "Error getting data", it)
        }
        userName.text=SaveSharedPreference.getUserName(applicationContext)



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(com.example.acropolis.R.menu.drawer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==com.example.acropolis.R.id.logout)
        {
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            SaveSharedPreference.setUserName(applicationContext,null)
            Toast.makeText(this, "Log out successfull", Toast.LENGTH_SHORT).show()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
data class data(
    val email: String?=null,
    val name: String?=null
)