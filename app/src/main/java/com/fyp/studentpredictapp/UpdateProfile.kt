package com.fyp.studentpredictapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fyp.studentpredictapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UpdateProfile : AppCompatActivity() {

    private lateinit var imageViewProfile: ImageView
    private lateinit var buttonSelectImage: Button
    private lateinit var buttonUpdate: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var editTextName: EditText
    private lateinit var editTextMajor: EditText
    private var imageUri: Uri? = null
    private lateinit var spinnerMajor: Spinner
    private var selectedMajor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        // Initialize Firestore and Storage
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference.child("profile_images")
        spinnerMajor = findViewById(R.id.spinnerMajor)
        val majorOptions = arrayOf("Intelligent Computing", "Software Engineering", "Computing Infrastructure")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, majorOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMajor.adapter = adapter
        spinnerMajor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handle item selection
                selectedMajor = majorOptions[position]
                // You can use selectedMajor as needed, such as storing it in a variable or sending it to Firestore
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection
            }
        }

        // Initialize views
        imageViewProfile = findViewById(R.id.imageViewProfile)
        buttonSelectImage = findViewById(R.id.buttonSelectImage)
        buttonUpdate = findViewById(R.id.buttonUpdate)
        editTextName = findViewById(R.id.editTextName)


        // Set click listener for the select image button
        buttonSelectImage.setOnClickListener {
            selectImage()
        }

        // Set click listener for the update button
        buttonUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageViewProfile.setImageURI(imageUri)
        }
    }

    private fun updateProfile() {


        val name = editTextName.text.toString().trim()
        val major = selectedMajor


            // Retrieve current user ID from Firebase Authentication
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            userId?.let { uid ->
                // Create a HashMap to hold the updated profile data
                val profileData = hashMapOf(
                    "name" to name,
                    "major" to major
                    // Add more fields as needed
                )

                // Update the profile in Firestore
                firestore.collection("users").document(uid)
                    .set(profileData)
                    .addOnSuccessListener {
                        showToast("Profile updated successfully")
                        val intent = Intent(this, DashboardPage::class.java)
                        startActivity(intent)

                        // If an image was selected, upload it to Firebase Storage
                        imageUri?.let { uri ->
                            val imageRef = storageRef.child("$uid/profile_image.jpg")
                            imageRef.putFile(uri)
                                .addOnSuccessListener {
                                    showToast("Profile image updated successfully")
                                }
                                .addOnFailureListener { e ->
                                    showToast("Failed to update profile image: ${e.message}")
                                    Log.e("Updateprofpic", "updateProfile: ${e.message}" ,)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        showToast("Failed to update profile: ${e.message}")
                    }
            }
        }


    // Function to display a toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_IMAGE = 101
    }
}
