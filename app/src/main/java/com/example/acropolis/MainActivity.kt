package com.example.acropolis

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.acropolis.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_home_screen.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!SaveSharedPreference.getUserName(applicationContext)!!.isEmpty())
        {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish()
        }


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
            val email=binding.SigninEmail.text.toString().trim()
            val password=binding.SigninPassword.text.toString().trim()
            if(email.isNotEmpty()&&password.isNotEmpty()){
                login(email,password)
            }
            else{
                Toast.makeText(this,"One of the entries is empty",Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnvalidatefromsignup.setOnClickListener {
            val email = binding.SignupEmail.text.toString().trim()
            val password = binding.SignupPassword.text.toString().trim()
            val userName=binding.SignUpUsername.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && userName.isNotEmpty()){
                if (isValidEmailId(email)){
                    if (password.length>5){
                        signup(email, password,userName)
                    }else{
                        Toast.makeText(this,"password length is less then six",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this,"invalid email address",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"One of the entries is empty",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
            if (task.isSuccessful){
                mDatabase=FirebaseDatabase.getInstance().getReference()
                mDatabase.child("user").child(mAuth.currentUser!!.uid).get().addOnSuccessListener {
                    val snap= it.getValue(data::class.java)!!
                    SaveSharedPreference.setUserName(applicationContext,snap.name)
                }
                if (mAuth.currentUser!!.isEmailVerified){
                    val intent = Intent(this, HomeScreen::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this,"please verify your email",Toast.LENGTH_SHORT).show()
                }

            }else{
                binding.forgotpassword.visibility=View.VISIBLE
                binding.forgotpassword.setOnClickListener {
                    val intent = Intent(this, PasswordReset::class.java)
                    startActivity(intent)
                }
                Toast.makeText(this,"email or password may be incorrect",Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "user already exist", Toast.LENGTH_SHORT).show()
                }

            }

    }
    private fun addUserToDatabase(email:String,name: String, uid: String) {
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase.child("user").child(uid).setValue(dataofperson(email,name))
    }
    private fun isValidEmailId(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

}

data class dataofperson(
    val email: String,
    val name: String
)