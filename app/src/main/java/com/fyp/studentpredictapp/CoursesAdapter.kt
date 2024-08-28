package com.fyp.studentpredictapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CoursesAdapter(
    private var courses: List<Pair<String, String>>,
    private val userId: String,
    private val firestore: FirebaseFirestore
) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {
    private var isEditMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.course_item_layout, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course)

        if (isEditMode) {
            holder.deleteButton.visibility = View.VISIBLE
        } else {
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount() = courses.size

    fun toggleEditMode() {
        isEditMode = !isEditMode
        notifyDataSetChanged()
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseCodeTextView: TextView = itemView.findViewById(R.id.courseCodeTextView)
        val courseNameTextView: TextView = itemView.findViewById(R.id.courseNameTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(course: Pair<String, String>) {
            courseCodeTextView.text = course.first
            courseNameTextView.text = course.second

            deleteButton.setOnClickListener {
                val courseCode = course.first
                deleteCourse(courseCode)
            }
        }

        private fun deleteCourse(courseCode: String) {
            firestore.collection("users")
                .document(userId)
                .collection("courses")
                .whereEqualTo("courseCode", courseCode)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentToDelete = querySnapshot.documents.first()
                        documentToDelete.reference.delete()
                            .addOnSuccessListener {
                                // Remove the deleted course from the adapter's data source
                                val updatedCourses = courses.filter { it.first != courseCode }
                                courses = updatedCourses.toMutableList()
                                notifyDataSetChanged()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("FirestoreError", "Error deleting course: ", exception)
                            }
                    } else {
                        Log.d("FirestoreError", "No document found with courseCode: $courseCode")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Error querying courses: ", exception)
                }
        }
    }
}