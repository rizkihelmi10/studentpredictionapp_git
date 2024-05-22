package com.fyp.studentpredictapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView

class CourseRegistFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var courseCode: String? = null
    private var courseName: String? = null
    private var lecturerName: String? = null
    private var courseType: String? = null
    private var numAssessments: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseCode = it.getString(ARG_COURSE_CODE)
            courseName = it.getString(ARG_COURSE_NAME)
            lecturerName = it.getString(ARG_LECTURER_NAME)
            courseType = it.getString(ARG_COURSE_TYPE)
            numAssessments = it.getString(ARG_NUM_ASSESSMENTS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_blank, container, false)

        // Get the number of assessments passed from the arguments
        arguments?.let {
            numAssessments
        }
        val numAssessmentsinteger = numAssessments?.toInt()

        // Get the LinearLayout container
        val linearLayoutContainer: LinearLayout = rootView.findViewById(R.id.fragment_container)

        // Add spinners dynamically based on the number of assessments
        for (i in 0 until (numAssessmentsinteger!!)) {
            // Inflate the layout for each spinner group
            val spinnerGroup = inflater.inflate(R.layout.spinner_group_layout, null)

            // Find the spinner views in the inflated layout
            val textViewAssessmentType: TextView = spinnerGroup.findViewById(R.id.textViewAssessmentType)
            val spinnerAssessmentType: Spinner = spinnerGroup.findViewById(R.id.spinnerAssessmentType)
            val textViewPercentage: TextView = spinnerGroup.findViewById(R.id.textViewPercentage)
            val spinnerPercentage: Spinner = spinnerGroup.findViewById(R.id.spinnerPercentage)

            // Set text for TextViews
            textViewAssessmentType.text = "Assessment Type ${i + 1}"
            textViewPercentage.text = "Percentage ${i + 1}"

            // Set adapter for spinners (assuming you have arrays defined)
            spinnerAssessmentType.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.first_spinner_items)
            )
            spinnerPercentage.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.second_spinner_items)
            )

            // Add the spinner group to the container
            linearLayoutContainer.addView(spinnerGroup)
        }

        return rootView
    }

    companion object {
        private const val ARG_COURSE_CODE = "courseCode"
        private const val ARG_COURSE_NAME = "courseName"
        private const val ARG_LECTURER_NAME = "lecturerName"
        private const val ARG_COURSE_TYPE = "courseType"
        private const val ARG_NUM_ASSESSMENTS = "numAssessments"
        @JvmStatic
        fun newInstance(
            courseCode: String,
            courseName: String,
            lecturerName: String,
            courseType: String,
            numAssessments: String
        ) =
            CourseRegistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_CODE, courseCode)
                    putString(ARG_COURSE_NAME, courseName)
                    putString(ARG_LECTURER_NAME, lecturerName)
                    putString(ARG_COURSE_TYPE, courseType)
                    putString(ARG_NUM_ASSESSMENTS, numAssessments)
                }
            }
    }
}