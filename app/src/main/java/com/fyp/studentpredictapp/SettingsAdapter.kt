package com.fyp.studentpredictapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SettingsAdapter(private val settingsOptions: List<SettingsOption>) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.settings_item, parent, false)
        return SettingsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val settingOption = settingsOptions[position]
        holder.bind(settingOption)
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