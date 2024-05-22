package com.fyp.studentpredictapp

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CourseAdapter(private val courses: List<String>,private val listener: OnItemClickListener) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    // ViewHolder class
    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val textCourseName: TextView = itemView.findViewById(R.id.textCourseName)

        init {
            // Set click listener on the item view
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            // Pass the click event to the listener
            val selectedCourse = textCourseName.text
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
                val context = itemView.context
                val intent = Intent(context, DetailedPrediction::class.java)
                intent.putExtra("selected_course", selectedCourse)
                Log.d("What have you select", "onItemClick: " + selectedCourse)
                context.startActivity(intent)

            }

        }

        fun bind(course: String, listener: OnItemClickListener) {
            textCourseName.text = course.toString()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course, listener)
    }

    override fun getItemCount(): Int {
        return courses.size
    }

}
