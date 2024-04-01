package com.fyp.studentpredictapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignupPage : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonSignup: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_page)
        editTextEmail = findViewById(R.id.editTextTextPersonName)
        editTextPassword = findViewById(R.id.editTextTextPersonName2)
        buttonSignup = findViewById(R.id.buttonsign)
        auth = FirebaseAuth.getInstance()

        buttonSignup.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Signup successful, navigate to some other activity
                            startActivity(Intent(this, LoginPage::class.java))
                            finish() // Finish SignupActivity to prevent returning to it by pressing back
                        } else {
                            // Signup failed, display a message to the user

                        }
                    }
            } catch (e: Exception) {
                // Handle the exception here, for example, display an error message
                Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }
    }
}