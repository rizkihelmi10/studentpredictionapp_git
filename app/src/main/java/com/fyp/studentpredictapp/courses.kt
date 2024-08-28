package com.fyp.studentpredictapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class courses : AppCompatActivity() {
    private lateinit var submitButton: Button
    private lateinit var checkBoxes: List<CheckBox>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var checkBoxContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var noCoursesTextView: TextView
    private lateinit var coursesAdapter: CoursesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        submitButton = findViewById(R.id.submitButton)
        noCoursesTextView = findViewById(R.id.noCoursesTextView)

        progressBar = findViewById(R.id.progressBar)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, DashboardPage::class.java)
            startActivity(intent)
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_menu_item -> {
                    toggleEditMode()
                    true
                }
                else -> false
            }
        }




        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid ->
            // Fetch courses from Firestore
            firestore.collection("users").document(uid).collection("courses")
                .get()
                .addOnSuccessListener { documents ->
                    if (documents != null && !documents.isEmpty) {
                        val courses = mutableListOf<Pair<String, String>>()

                        for (document in documents) {
                            val courseCode = document.getString("courseCode")
                            val courseName = document.getString("courseName")
                            if (courseCode != null && courseName != null) {
                                courses.add(Pair(courseCode, courseName))
                            }
                        }

                        if (courses.isEmpty()) {
                            noCoursesTextView.visibility = View.VISIBLE
                        } else {
                            noCoursesTextView.visibility = View.GONE
                            // Display the courses (e.g., in a RecyclerView or ListView)
                            displayCourses(courses)
                            submitButton.setText("Add courses")
                        }
                    } else {
                        noCoursesTextView.visibility = View.VISIBLE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Error fetching courses: ", exception)
                }
        }

      /*  showLoading()*/
       /* generateCourseList()*/
        submitButton.setOnClickListener {
            val intent = Intent(this, RegisterCourse::class.java)
            startActivity(intent)
        }
    }
    fun toggleEditMode() {
        coursesAdapter.toggleEditMode()
    }
    private fun displayCourses(courses: List<Pair<String, String>>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            coursesAdapter = CoursesAdapter(courses.toMutableList(), uid, firestore)
            recyclerView.adapter = coursesAdapter
            val dividerItemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
            recyclerView.addItemDecoration(dividerItemDecoration)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

}