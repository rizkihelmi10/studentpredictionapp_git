package com.fyp.studentpredictapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        firebaseAuth = FirebaseAuth.getInstance()

        // Setup toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarpred)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Create list of settings options with text and icon
        val settingsOptions = listOf(
            SettingsOption("Sign out", R.drawable.person_24px),
            SettingsOption("Course Management", R.drawable.library_books_24px)
        )


        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SettingsAdapter(
            settingsOptions,
            this,
            firebaseAuth,
            { navigateToLoginScreen() },
            { navigateToCourseActivity() }
        )
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
    private fun navigateToLoginScreen() {
        // Navigate to the login screen or perform any other necessary actions after sign out
        val intent = Intent(this, LoginPage::class.java)
        startActivity(intent)
        finish()
    }
    private fun navigateToCourseActivity() {
        val intent = Intent(this, courses::class.java)
        startActivity(intent)
    }

}