package com.example.acropolis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.acropolis.databinding.ActivityPasswordResetBinding
import com.google.firebase.auth.FirebaseAuth

class PasswordReset : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordResetBinding
    lateinit var mAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth=FirebaseAuth.getInstance()
        binding.btnSendEmail.setOnClickListener {
            val email=binding.forgotEmail.text.toString()
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task->
                if (task.isSuccessful){
                    Toast.makeText(this, "Email sent successfully", Toast.LENGTH_SHORT).show()
                    val intent=Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}