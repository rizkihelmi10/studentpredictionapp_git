package com.fyp.studentpredictapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, navigate to some other activity
            startActivity(Intent(this, DashboardPage::class.java))
            finish() // Finish MainActivity to prevent returning to it by pressing back
        } else {
            // User is not signed in, navigate to login activity
            startActivity(Intent(this, LoginPage::class.java))
            finish() // Finish MainActivity to prevent returning to it by pressing back
        }
    }
}