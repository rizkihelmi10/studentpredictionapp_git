package com.fyp.studentpredictapp

import android.os.Bundle
import android.util.Log
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
import com.jjoe64.graphview.helper.StaticLabelsFormatter

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

        setupCourseSpinner()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupCourseSpinner() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid ->
            val coursesRef = firestore.collection("users").document(uid).collection("courses")
            coursesRef.get().addOnSuccessListener { querySnapshot ->
                val courseNames = querySnapshot.documents.map { it.getString("courseName") ?: "" }
                setupSpinnerAdapter(courseNames)
            }.addOnFailureListener { exception ->
                showToast("Error fetching courses: $exception")
            }
        }
    }

    private fun setupSpinnerAdapter(courseNames: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = adapter

        courseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCourse = courseNames[position]
                selectedCourse?.let { fetchCourseData(it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchCourseData(courseName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            firestore.collection("users").document(uid)
                .collection("courses").whereEqualTo("courseName", courseName)
                .get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val courseData = document.data

                        if (courseData != null) {
                            val seriesData = mutableListOf<DataPoint>()

                            // Extract assessments
                            val assessments = (courseData["assessments"] as? List<Map<String, Any>>)
                                ?.associate { it["assessmentType"] as String to it["score"] as Double }
                                ?: emptyMap()

                            Log.d("Assessments", "Assessments: $assessments")

                            // Extract other fields
                            val cwScore = (courseData["coursework"] as? Double) ?: 0.0
                            val finalScore = (courseData["predictedScore"] as? Double) ?: 0.0
                            val finalExam = (courseData["predictedFinal"] as? Double) ?: 0.0
                            val doubfinalScore = finalScore.toDouble()
                            val doubfinalExam = finalExam.toDouble()
                            Log.d("Scores", "Coursework: $cwScore, Final Score: $doubfinalScore, Final Exam: $doubfinalExam")

                            // Add base point
                            seriesData.add(DataPoint(0.0, 0.0))

                            // Add assessment points
                            val labels = mutableListOf<String>("Start")
                            if (assessments.isNotEmpty()) {
                                assessments.entries.forEachIndexed { index, entry ->
                                    seriesData.add(DataPoint((index + 1).toDouble(), entry.value))
                                    labels.add(entry.key)
                                    Log.d("AssessmentPoint", "Index: ${index + 1}, Value: ${entry.value}")
                                }
                            } else {
                                Log.w("Assessments", "No assessments data found")
                            }

                            // Add coursework, final score, and final exam
                            val offset = assessments.size + 1
                            seriesData.add(DataPoint(offset.toDouble(), cwScore))
                            labels.add("Coursework")
                            seriesData.add(DataPoint((offset + 1).toDouble(), finalScore))
                            labels.add("Final Score")
                            seriesData.add(DataPoint((offset + 2).toDouble(), finalExam))
                            labels.add("Final Exam")

                            // Update the graph with the retrieved data
                            updateGraph(courseName, seriesData, labels.toTypedArray())
                        } else {
                            Log.w("CourseData", "Course data is null")
                            showToast("Course data is null")
                        }
                    } else {
                        Log.w("CourseData", "Course data not found")
                        showToast("Course data not found")
                    }
                }.addOnFailureListener { exception ->
                    Log.e("CourseData", "Error fetching course data", exception)
                    showToast("Error fetching course data: $exception")
                }
        }


    }

    private fun updateGraph(courseName: String, seriesData: List<DataPoint>, labels: Array<String>) {
        // Create series from seriesData and add it to the graph
        val series = LineGraphSeries<DataPoint>(seriesData.toTypedArray())
        graphView.title = "$courseName Score Graph"
        graphView.titleTextSize = 20F
        graphView.removeAllSeries()
        graphView.addSeries(series)

        // Set up labels for the X-axis
        val staticLabelsFormatter = StaticLabelsFormatter(graphView)
        staticLabelsFormatter.setHorizontalLabels(labels)
        graphView.gridLabelRenderer.labelFormatter = staticLabelsFormatter
    }
}
