package com.fyp.studentpredictapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputLayout


class RegisterCourse : AppCompatActivity() {
    private lateinit var textInputLayoutCourseCode: TextInputLayout
    private lateinit var textInputLayoutCourseName: TextInputLayout
    private lateinit var textInputLayoutLecturerName: TextInputLayout
    private lateinit var editTextCourseCode: EditText
    private lateinit var editTextCourseName: EditText
    private lateinit var editTextLecturerName: EditText
    private lateinit var spinnerCourseType: Spinner
    private lateinit var spinnerNumAssessments: Spinner
    private lateinit var spinnertextviewcourse: TextView
    private lateinit var spinnertextviewNumAssesments: TextView
    private lateinit var nextButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_course)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        textInputLayoutCourseCode = findViewById(R.id.textInputLayoutCourseCode);
        textInputLayoutCourseName = findViewById(R.id.textInputLayoutCourseName);
        textInputLayoutLecturerName = findViewById(R.id.textInputLayoutLecturerName);
        editTextCourseCode = findViewById(R.id.editTextCourseCode);
        editTextCourseName = findViewById(R.id.editTextCourseName);
        editTextLecturerName = findViewById(R.id.editTextLecturerName);
        spinnerCourseType = findViewById(R.id.spinnerCourseType);
        spinnerNumAssessments = findViewById(R.id.spinnerNumAssessments);
        spinnertextviewNumAssesments = findViewById(R.id.textViewNumAssessments)
        spinnertextviewcourse = findViewById(R.id.textViewCourseType)
        nextButton = findViewById(R.id.nextButton)

        nextButton.setOnClickListener {
            val courseCode = editTextCourseCode.text.toString()
            val courseName = editTextCourseName.text.toString()
            val lecturerName = editTextLecturerName.text.toString()
            val courseType = spinnerCourseType.selectedItem.toString()
            val numAssessments = spinnerNumAssessments.selectedItem.toString()

            val intent = Intent(this, CourseRegist::class.java).apply {
                putExtra(ARG_COURSE_CODE, courseCode)
                putExtra(ARG_COURSE_NAME, courseName)
                putExtra(ARG_LECTURER_NAME, lecturerName)
                putExtra(ARG_COURSE_TYPE, courseType)
                putExtra(ARG_NUM_ASSESSMENTS, numAssessments)
            }
            startActivity(intent)

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