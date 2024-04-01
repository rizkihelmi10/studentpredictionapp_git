package com.fyp.studentpredictapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class courses : AppCompatActivity() {
    private lateinit var submitButton: Button
    private lateinit var checkBoxes: List<CheckBox>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var checkBoxContainer: LinearLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        submitButton = findViewById(R.id.submitButton)
        checkBoxes = listOf(
            findViewById(R.id.checkBox),
            findViewById(R.id.checkBox2),
            findViewById(R.id.checkBox3),
            findViewById(R.id.checkBox4),
            findViewById(R.id.checkBox5),
            findViewById(R.id.checkBox6),
            findViewById(R.id.checkBox7)
        )
        progressBar = findViewById(R.id.progressBar)
        checkBoxContainer = findViewById(R.id.checkBox_container)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, DashboardPage::class.java)
            startActivity(intent)
        }


        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()
        showLoading()
        generateCourseList()
        submitButton.setOnClickListener {
            updateCourses()
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun generateCourseList() {
        firestore = FirebaseFirestore.getInstance()

        // Retrieve current user ID from Firebase Authentication
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            // Fetch profile data from Firestore
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val major = document.getString("major")

                        // Display profile data if not default values
                        if (major == "Intelligent Computing") {
                            val firestore = FirebaseFirestore.getInstance()
                            val collectionName =
                                "Intelligent Computing" // Collection name in Firestore

                            for (i in 0 until 8) { // Iterate from 0 to 7
                                val documentName = "coursesIC" // Document name in Firestore
                                firestore.collection(collectionName).document(documentName)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document != null && document.exists()) {
                                            val course =
                                                document.getString(i.toString()) // Retrieve the course for the current index
                                            if (course != null && i < checkBoxes.size) {
                                                checkBoxes[i].text = course
                                            }
                                        }
                                        hideLoading()
                                    }

                                    .addOnFailureListener { exception ->
                                        hideLoading()
                                    }
                            }
                        } else if (major == "Software Engineering") {
                            val firestore = FirebaseFirestore.getInstance()
                            val collectionName =
                                "Software Engineering" // Collection name in Firestore

                            for (i in 0 until 8) { // Iterate from 0 to 7
                                val documentName = "CoursesSE" // Document name in Firestore
                                firestore.collection(collectionName).document(documentName)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document != null && document.exists()) {
                                            val course =
                                                document.getString(i.toString()) // Retrieve the course for the current index
                                            if (course != null && i < checkBoxes.size) {
                                                checkBoxes[i].text = course
                                            }
                                        }
                                        hideLoading()
                                    }
                                    .addOnFailureListener { exception ->
                                        hideLoading()
                                    }
                            }

                        } else {
                            val firestore = FirebaseFirestore.getInstance()
                            val collectionName =
                                "Computer Infrastructure" // Collection name in Firestore

                            for (i in 0 until 8) { // Iterate from 0 to 7
                                val documentName = "CoursesCI" // Document name in Firestore
                                firestore.collection(collectionName).document(documentName)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document != null && document.exists()) {
                                            val course =
                                                document.getString(i.toString()) // Retrieve the course for the current index
                                            if (course != null && i < checkBoxes.size) {
                                                // Update the text of the checkbox at index i with the retrieved course name
                                                checkBoxes[i].text = course
                                            }
                                        }
                                        hideLoading()
                                    }
                                    .addOnFailureListener { exception ->
                                        hideLoading()
                                    }
                            }

                        }
                    }
                }
                .addOnFailureListener { e ->
                    hideLoading()
                }
        }

    }

    private fun createCheckbox(courseName: String, index: Int) {
        val checkBox = CheckBox(this)
        checkBox.text = courseName
        checkBoxContainer.addView(checkBox, index)
    }


    @SuppressLint("SuspiciousIndentation")
    private fun updateCourses() {
        val selectedCourses = mutableListOf<String>()

        // Get the selected courses
        for (checkBox in checkBoxes) {
            if (checkBox.isChecked) {
                selectedCourses.add(checkBox.text.toString())
            }
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userCoursesRef = firestore.collection("users").document(userId)
            userCoursesRef.update("courses", selectedCourses)
                .addOnSuccessListener {
                    // Courses updated successfully
                    // You can show a success message or navigate to another activity
                }
                .addOnFailureListener { e ->
                    // Error updating courses
                    // You can show an error message or log the error
                }
        }
    }
}