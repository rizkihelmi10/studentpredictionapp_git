package com.fyp.studentpredictapp


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginPage : AppCompatActivity() {
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textViewDontHaveAccount: TextView
    private lateinit var textViewSignUp: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)
        editTextUsername = findViewById(R.id.editTextTextPersonName)
        editTextPassword = findViewById(R.id.editTextTextPersonName2)
        buttonLogin = findViewById(R.id.buttonlogin)
        textViewDontHaveAccount = findViewById(R.id.donthaveaccount)
        textViewSignUp = findViewById(R.id.Signup)
        auth = FirebaseAuth.getInstance()
        
        buttonLogin.setOnClickListener {
            val email = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login successful, navigate to some other activity
                        startActivity(Intent(this, DashboardPage::class.java))
                        finish() // Finish LoginActivity to prevent returning to it by pressing back
                    } else {
                        // Login failed, display a message to the user
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        textViewSignUp.setOnClickListener {
            // Handle click event, for example, navigate to the signup activity
            val intent = Intent(this, SignupPage::class.java)
            startActivity(intent)
        }


    }

}