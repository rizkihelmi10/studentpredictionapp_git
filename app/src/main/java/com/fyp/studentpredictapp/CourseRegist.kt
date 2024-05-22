package com.fyp.studentpredictapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class CourseRegist : AppCompatActivity() {
    private var courseCode: String? = null
    private var courseName: String? = null
    private var lecturerName: String? = null
    private var courseType: String? = null
    private var numAssessments: String? = null
    private lateinit var submitButton: Button
    private lateinit var firestore: FirebaseFirestore


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_regist)

        // Retrieve data from Intent extras
        courseCode = intent.getStringExtra(ARG_COURSE_CODE)
        courseName = intent.getStringExtra(ARG_COURSE_NAME)
        lecturerName = intent.getStringExtra(ARG_LECTURER_NAME)
        courseType = intent.getStringExtra(ARG_COURSE_TYPE)
        numAssessments = intent.getStringExtra(ARG_NUM_ASSESSMENTS)
        Toast.makeText(this, numAssessments, Toast.LENGTH_SHORT).show()
        submitButton = findViewById(R.id.submit_button)

        val numAssessmentsInteger = numAssessments?.toInt() ?: 0
       // Toast.makeText(this, "num:" + numAssessmentsInteger, Toast.LENGTH_SHORT).show()
        Log.d("What inthoy", "onCreate: " + numAssessmentsInteger)
        // Get the LinearLayout container
        val linearLayoutContainer: LinearLayout = findViewById(R.id.container_layout)
        var assessmentType: String? = null
        var percentage: String? = null

        // Add spinners dynamically based on the number of assessments
        val assessmentTypes = mutableListOf<String>()
        val percentages = mutableListOf<String>()

        for (i in 0 until numAssessmentsInteger) {
            // Inflate the layout for each spinner group
            val spinnerGroup = layoutInflater.inflate(R.layout.spinner_group_layout, null)
            val textViewAssessmentType: TextView = spinnerGroup.findViewById(R.id.textViewAssessmentType)
            val spinnerAssessmentType: Spinner = spinnerGroup.findViewById(R.id.spinnerAssessmentType)
            val textViewPercentage: TextView = spinnerGroup.findViewById(R.id.textViewPercentage)
            val spinnerPercentage: Spinner = spinnerGroup.findViewById(R.id.spinnerPercentage)

            // Set text for TextViews
            textViewAssessmentType.text = "Assessment Type ${i + 1}"
            textViewPercentage.text = "Percentage ${i + 1}"

            // Set adapter for spinners (assuming you have arrays defined)
            spinnerAssessmentType.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.first_spinner_items,
                android.R.layout.simple_spinner_dropdown_item
            )
            spinnerPercentage.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.second_spinner_items,
                android.R.layout.simple_spinner_dropdown_item
            )

            // Add the spinner group to the container
            linearLayoutContainer.addView(spinnerGroup)

            // Initialize the lists with default selections
            assessmentTypes.add(spinnerAssessmentType.selectedItem.toString())
            percentages.add(spinnerPercentage.selectedItem.toString())

            // Set listeners to update the lists when selections change
            spinnerAssessmentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    assessmentTypes[i] = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            spinnerPercentage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    percentages[i] = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        submitButton.setOnClickListener {
            if (assessmentTypes.isNotEmpty() && percentages.isNotEmpty()) {
                // Pass the lists to your function
                submitCourseData(numAssessmentsInteger, assessmentTypes, percentages)
                val intent = Intent(this, courses::class.java)
                startActivity(intent)
            }
            Log.d("what passed", "Assessment Types: $assessmentTypes Percentages: $percentages")
        }
    }

    private fun submitCourseData(numassessments: Int, assesstype: MutableList<String>, assesspercent: MutableList<String>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        firestore = FirebaseFirestore.getInstance()

        userId?.let { uid ->
            // Create a reference to the user's document in "users" collection
            val userDocRef = firestore.collection("users").document(uid)

            // Create a new document reference for the course under the user's document
            val courseDocRef = userDocRef.collection("courses").document()

            // Create a map to hold the course data
            val courseData = hashMapOf(
                "courseCode" to courseCode,
                "courseName" to courseName,
                "lecturerName" to lecturerName,
                "courseType" to courseType,
                "numAssessments" to numAssessments
            )

            // Create a list to hold all assessment data
            val assessments = mutableListOf<Map<String, String>>()

            for (i in 0 until numassessments) {
                val assessmentData = hashMapOf(
                    "assessmentType" to assesstype[i],
                    "percentage" to assesspercent[i]
                )
                assessments.add(assessmentData)
            }

            // Create a map to hold the assessments array
            val assessmentsData = hashMapOf(
                "assessments" to assessments
            )

            // Set the course data to the course document reference
            courseDocRef.set(courseData, SetOptions.merge())
                .addOnSuccessListener {
                    // Add the assessments array to the course document reference
                    courseDocRef.collection("assessments")
                        .document("all_assessments")
                        .set(assessmentsData, SetOptions.merge())
                        .addOnSuccessListener {
                            // Assessments data successfully written
                            Log.d("Checkingnow", "Assessments document added")
                        }
                        .addOnFailureListener { e ->
                            // Handle errors while writing assessments data
                            Log.e("Checkingnow", "Error adding assessments document", e)
                        }
                }
                .addOnFailureListener { e ->
                    // Handle errors while writing course data
                    Log.e("Checkingnow", "Error adding course document", e)
                }
        }
    }



    companion object {
        private const val ARG_COURSE_CODE = "courseCode"
        private const val ARG_COURSE_NAME = "courseName"
        private const val ARG_LECTURER_NAME = "lecturerName"
        private const val ARG_COURSE_TYPE = "courseType"
        private const val ARG_NUM_ASSESSMENTS = "numAssessments"
    }
}
