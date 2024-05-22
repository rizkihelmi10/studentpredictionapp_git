package com.fyp.studentpredictapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CourseAdapter2(private val courses: List<Course>, private val listener: OnItemClickListener) : RecyclerView.Adapter<CourseAdapter2.CourseViewHolder>() {
    data class Course(val courseCode: String, val courseName: String)

    interface OnItemClickListener {
        fun onItemClick(course: Course)
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseCodeTextView: TextView = itemView.findViewById(R.id.courseCodeTextView)
        val courseNameTextView: TextView = itemView.findViewById(R.id.courseNameTextView)

        fun bind(course: Course, listener: OnItemClickListener) {
            courseCodeTextView.text = course.courseCode
            courseNameTextView.text = course.courseName
            itemView.setOnClickListener {
                listener.onItemClick(course)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.course_item_layout2, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course, listener)
    }

    override fun getItemCount() = courses.size
}