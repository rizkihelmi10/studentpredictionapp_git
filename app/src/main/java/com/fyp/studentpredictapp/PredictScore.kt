package com.fyp.studentpredictapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PredictScore : AppCompatActivity(), CourseAdapter.OnItemClickListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourseAdapter
    private val coursesList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predict_score)

        // Initialize FirebaseFirestore instance
        firestore = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CourseAdapter(coursesList, this)
        recyclerView.adapter = adapter

        // Retrieve Firestore instance
        firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid ->
            val userRef = firestore.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Extract courses field from the document
                        val courses = document["courses"] as? List<String>
                        if (courses != null) {
                            coursesList.addAll(courses)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        // Handle document not found
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                }
        }

        // Retrieve courses for the specified user

    }


    override fun onItemClick(position: Int) {
        val selectedCourse = coursesList[position]
        val intent = Intent(this, DetailedPrediction::class.java)
        intent.putExtra("selected_course", selectedCourse)
        Log.d("What have you select", "onItemClick: " + selectedCourse)
        startActivity(intent)
    }
}


