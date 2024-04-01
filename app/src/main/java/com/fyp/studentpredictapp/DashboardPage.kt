package com.fyp.studentpredictapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.fyp.studentpredictapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardPage : AppCompatActivity() {
    // Define your variables here
    private lateinit var contributeCard: CardView
    private lateinit var practiceCard: CardView
    private lateinit var learnCard: CardView
    private lateinit var interestsCard: CardView
    private lateinit var helpCard: CardView
    private lateinit var settingsCard: CardView
    private lateinit var profileB: ImageButton
    private lateinit var textView: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView1: TextView
    private lateinit var textView20: TextView
    private lateinit var textView21: TextView
    private lateinit var textView22: TextView
    private lateinit var textView23: TextView
    private lateinit var textView4: TextView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var updateprofilebutton: Button
    private lateinit var logo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_page)


        // Initialize your views here
        contributeCard = findViewById(R.id.contributeCard)
        practiceCard = findViewById(R.id.practiceCard)
        learnCard = findViewById(R.id.learnCard)
        interestsCard = findViewById(R.id.interestsCard)
        helpCard = findViewById(R.id.helpCard)
        settingsCard = findViewById(R.id.settingsCard)
        profileB = findViewById(R.id.profileB)
        textView = findViewById(R.id.textView)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView3)
        textView1 = findViewById(R.id.textView1)
        textView20 = findViewById(R.id.textView20)
        textView21 = findViewById(R.id.textView21)
        textView22 = findViewById(R.id.textView22)
        textView23 = findViewById(R.id.textView23)
        textView4 = findViewById(R.id.textView4)
        updateprofilebutton = findViewById(R.id.editProfileB)
        logo = findViewById(R.id.AppLogo)
        val bitmap = (resources.getDrawable(R.drawable.student) as BitmapDrawable).bitmap
        val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(imageRounded)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawRoundRect(
            RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
            200F, 200F, paint) // Round Image Corner 100 100 100 100
        logo.setImageBitmap(imageRounded)

        // Set OnClickListener or other necessary configurations for your views here
        // For example:
        profileB.setOnClickListener {
            // Handle profile button click
        }
        updateprofilebutton.setOnClickListener {
            val intent = Intent(this, UpdateProfile::class.java)
            startActivity(intent)
        }
        practiceCard.setOnClickListener {
           val intent = Intent(this, courses::class.java)
            startActivity(intent)
        }
        contributeCard.setOnClickListener {
            val intent = Intent(this, PredictScore::class.java)
            startActivity(intent)
        }
        interestsCard.setOnClickListener {
            val intent = Intent(this, Analytics::class.java)
            startActivity(intent)
        }
        learnCard.setOnClickListener {
            val intent = Intent(this, LearnActivity::class.java)
            startActivity(intent)
        }
        userData()


    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("You have not complete your profile")
        builder.setPositiveButton("Update") { dialog, which ->
            val intent = Intent(this, UpdateProfile::class.java)
            startActivity(intent)

        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // You can handle button clicks here
            dialog.dismiss() // Dismiss the dialog
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun userData() {
        firestore = FirebaseFirestore.getInstance()

        // Retrieve current user ID from Firebase Authentication
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            // Fetch profile data from Firestore
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name")
                        val major = document.getString("major")

                        // Display profile data if not default values
                        if (name != "FIRSTNAME LASTNAME" && major != "Major Name") {
                            textView2.text = name
                            textView3.text = major
                        } else {
                            // Show alert if profile is not completed
                            showAlert()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle any errors
                }
        }
    }
}
