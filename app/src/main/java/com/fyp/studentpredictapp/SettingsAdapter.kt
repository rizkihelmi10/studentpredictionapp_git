package com.fyp.studentpredictapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class SettingsAdapter(
    private val settingsOptions: List<SettingsOption>,
    private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val onSignOutListener: () -> Unit,
    private val onCourseManagementListener: () -> Unit
) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.settings_item, parent, false)
        return SettingsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val settingOption = settingsOptions[position]
        holder.bind(settingOption)
        if (settingOption.text == "Sign out") {
            holder.itemView.setOnClickListener {
                showSignOutDialog()
            }
        }else if (settingOption.text == "Course Management") {
            holder.itemView.setOnClickListener {
                onCourseManagementListener.invoke()
            }
        }
    }

    private fun showSignOutDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Sign Out")
        builder.setMessage("Are you sure you want to sign out?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Sign out the user
            firebaseAuth.signOut()
            // Invoke the onSignOutListener
            onSignOutListener.invoke()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    override fun getItemCount() = settingsOptions.size

    class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val optionTextView: TextView = itemView.findViewById(R.id.optionTextView)
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)

        fun bind(settingOption: SettingsOption) {
            optionTextView.text = settingOption.text
            iconImageView.setImageResource(settingOption.iconResId)
        }
    }
}