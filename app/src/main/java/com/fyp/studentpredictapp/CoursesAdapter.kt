package com.fyp.studentpredictapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CoursesAdapter(private val courses: List<Pair<String, String>>) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseCodeTextView: TextView = itemView.findViewById(R.id.courseCodeTextView)
        val courseNameTextView: TextView = itemView.findViewById(R.id.courseNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.course_item_layout, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val (courseCode, courseName) = courses[position]
        holder.courseCodeTextView.text = courseCode
        holder.courseNameTextView.text = courseName
    }

    override fun getItemCount() = courses.size
}
