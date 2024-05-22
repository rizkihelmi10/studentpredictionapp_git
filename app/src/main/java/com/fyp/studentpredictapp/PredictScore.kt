package com.fyp.studentpredictapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
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

class PredictScore : AppCompatActivity(), CourseAdapter2.OnItemClickListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourseAdapter2
    private val coursesList = mutableListOf<CourseAdapter2.Course>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predict_score)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // Initialize FirebaseFirestore instance
        firestore = FirebaseFirestore.getInstance()

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CourseAdapter2(coursesList, this)
        recyclerView.adapter = adapter

        // Retrieve Firestore instance
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid ->
            val userRef = firestore.collection("users").document(uid)
            userRef.collection("courses")
                .get()
                .addOnSuccessListener { documents ->
                    if (documents != null && !documents.isEmpty) {
                        for (document in documents) {
                            val courseCode = document.getString("courseCode")
                            val courseName = document.getString("courseName")
                            if (courseCode != null && courseName != null) {
                                coursesList.add(CourseAdapter2.Course(courseCode, courseName))
                            }
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        // Handle no courses case
                        findViewById<TextView>(R.id.noCoursesTextView).visibility = View.VISIBLE
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    Log.e("FirestoreError", "Error fetching courses: ", exception)
                }
        }
        toolbar.setNavigationOnClickListener(){
            val intent = Intent(this, DashboardPage::class.java)
            startActivity(intent)
        }
    }

    override fun onItemClick(course: CourseAdapter2.Course) {
        // Handle item click
        Toast.makeText(this, "Selected: ${course.courseCode} - ${course.courseName}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DetailedPrediction::class.java)
        val courseCodePass = course.courseCode.toString()
        val courseNamePass = course.courseName
        intent.putExtra("selected_course", courseCodePass)
        intent.putExtra("selected_course_name", courseNamePass)
        startActivity(intent)

    }
}



 /*   override fun onItemClick(position: Int) {
        val selectedCourse = coursesList[position]
        val intent = Intent(this, DetailedPrediction::class.java)
        intent.putExtra("selected_course", selectedCourse)
        Log.d("What have you select", "onItemClick: " + selectedCourse)
        startActivity(intent)
    }
}*/


