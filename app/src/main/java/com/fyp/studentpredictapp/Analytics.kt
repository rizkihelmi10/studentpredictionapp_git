package com.fyp.studentpredictapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries


class Analytics : AppCompatActivity() {
    private lateinit var graphView: GraphView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var courseSpinner: Spinner
    private var selectedCourse: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)
        firestore = FirebaseFirestore.getInstance()

        graphView = findViewById(R.id.graph)
        courseSpinner = findViewById(R.id.spinnerCourses)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        droppedDownAdapter()
       /* try {
            firestore.collection("users").document(userId!!)
                .collection("courses").document("courseName")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val courseData = document.data
                        val seriesData = mutableListOf<DataPoint>()

                        // Retrieve data from Firestore document
                        val test1Score = (courseData?.get("test1") as? Long)?.toDouble() ?: 0.0
                        val test2Score = (courseData?.get("test2") as? Long)?.toDouble() ?: 0.0
                        val assign1Score = (courseData?.get("assign1") as? Long)?.toDouble() ?: 0.0
                        val assign2Score = (courseData?.get("assign2") as? Long)?.toDouble() ?: 0.0
                        val cwScore = (courseData?.get("CW") as? Long)?.toDouble() ?: 0.0
                        val finalScore = (courseData?.get("finalscore") as? Long)?.toDouble() ?: 0.0
                        val finalExam = (courseData?.get("finalexam") as? Long)?.toDouble() ?: 0.0

                        // Add retrieved data to seriesData list
                        seriesData.add(DataPoint(0.0, 0.0)) // Assuming this is the base point
                        seriesData.add(DataPoint(1.0, test1Score))
                        seriesData.add(DataPoint(2.0, test2Score))
                        seriesData.add(DataPoint(3.0, assign1Score))
                        seriesData.add(DataPoint(4.0, assign2Score))
                        seriesData.add(DataPoint(5.0, cwScore))
                        seriesData.add(DataPoint(6.0, finalScore))
                        seriesData.add(DataPoint(7.0, finalExam))

                        val courseName = (courseData?.get("coursename") as String)

                        // Create series from seriesData and add it to the graph
                        val series = LineGraphSeries<DataPoint>(seriesData.toTypedArray())
                        graphView.setTitle(courseName + " Score Graph")
                        graphView.setTitleTextSize(20F)
                        graphView.addSeries(series)
                    } else {
                        showToast("Document does not exist")
                    }
                }
                .addOnFailureListener { exception ->
                    showToast("Error fetching document: $exception")
                }
        } catch (e: IllegalArgumentException) {
        }*/
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun droppedDownAdapter() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid ->
            val userRef = firestore.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Extract courses field from the document
                        val courses = document["courses"] as? List<String>
                        if (courses != null) {
                            // Set up the spinner adapter with the retrieved course names
                            val adapter = ArrayAdapter(
                                this,
                                android.R.layout.simple_spinner_item,
                                courses
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            courseSpinner.adapter = adapter

                            // Set up item selection listener for the spinner
                            courseSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        // Fetch course data for the selected course name
                                        val selectedCourseName = courses[position]
                                        fetchCourseData(selectedCourseName)
                                    }

                                    override fun onNothingSelected(p0: AdapterView<*>?) {
                                    }
                                }
                        }
                    } else {
                        // Handle document not found
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                }
        }
    }

    private fun fetchCourseData(courseName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid ->
            // Replace invalid characters in courseName
            val validCourseName = courseName.replace("/", "-")
            showToast(validCourseName)
            firestore.collection("users").document(uid)
                .collection("courses").document(validCourseName)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Retrieve course data for the selected course name
                        val courseData = document.data
                        val seriesData = mutableListOf<DataPoint>()

                        // Retrieve data from Firestore document
                        val test1Score = (courseData?.get("test1") as? Long)?.toDouble() ?: 0.0
                        val test2Score = (courseData?.get("test2") as? Long)?.toDouble() ?: 0.0
                        val assign1Score = (courseData?.get("assign1") as? Long)?.toDouble() ?: 0.0
                        val assign2Score = (courseData?.get("assign2") as? Long)?.toDouble() ?: 0.0
                        val cwScore = (courseData?.get("CW") as? Long)?.toDouble() ?: 0.0
                        val finalScore = (courseData?.get("finalscore") as? Long)?.toDouble() ?: 0.0
                        val finalExam = (courseData?.get("finalexam") as? Long)?.toDouble() ?: 0.0

                        // Add retrieved data to seriesData list
                        seriesData.add(DataPoint(0.0, 0.0)) // Assuming this is the base point
                        seriesData.add(DataPoint(1.0, test1Score))
                        seriesData.add(DataPoint(2.0, test2Score))
                        seriesData.add(DataPoint(3.0, assign1Score))
                        seriesData.add(DataPoint(4.0, assign2Score))
                        seriesData.add(DataPoint(5.0, cwScore))
                        seriesData.add(DataPoint(6.0, finalScore))
                        seriesData.add(DataPoint(7.0, finalExam))

                        // Update the graph with the retrieved data
                        updateGraph(courseName, seriesData)
                    } else {
                        showToast("Course data not found")
                    }
                }
                .addOnFailureListener { exception ->
                    showToast("Error fetching course data: $exception")
                }
        }
    }

    private fun updateGraph(courseName: String, seriesData: List<DataPoint>) {
        // Create series from seriesData and add it to the graph
        val series = LineGraphSeries<DataPoint>(seriesData.toTypedArray())
        graphView.setTitle("$courseName Score Graph")
        graphView.setTitleTextSize(20F)
        graphView.removeAllSeries()
        graphView.addSeries(series)
    }
}