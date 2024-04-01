package com.fyp.studentpredictapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.FloatBuffer


class DetailedPrediction : AppCompatActivity() {
    lateinit var module: Module
    lateinit var module2: Module
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_prediction)
        val courseName = intent.getStringExtra("selected_course")
        Log.d("What have you select", "received: " + courseName)
        try {
            module = LiteModuleLoader.load(assetFilePath("model.ptl"))
        } catch (e: IOException) {
            Log.e("tryload", "Unable to load model", e)
        }
        try {
            module2 = LiteModuleLoader.load(assetFilePath("model2.ptl"))
        } catch (e: IOException) {
            Log.e("tryload", "Unable to load model", e)
        }
        firestore = FirebaseFirestore.getInstance()
        val textViewTitle: TextView = findViewById(R.id.textViewTitle)
        val textViewDescription: TextView = findViewById(R.id.textViewDescription)
        val coursename: TextView = findViewById(R.id.coursename)
        val textViewFinalScoreTitle: TextView = findViewById(R.id.textViewFinalScoreTitle)
        val textViewRequiredExamScoreTitle: TextView = findViewById(R.id.textViewRequiredExamScoreTitle)
        val Test1tx: TextView = findViewById(R.id.Test1tx)
        val Test2tx: TextView = findViewById(R.id.Test2tx)
        val assign1tx: TextView = findViewById(R.id.assign1tx)
        val assign2tx: TextView = findViewById(R.id.assign2tx)
        val cwtx: TextView = findViewById(R.id.cwtx)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbarpred)


        val Test1: EditText = findViewById(R.id.Test1)
        val Test2: EditText = findViewById(R.id.Test2)
        val assign1: EditText = findViewById(R.id.assign1)
        val assign2: EditText = findViewById(R.id.assign2)
        val cw: EditText = findViewById(R.id.cw)

        val buttonPredict: Button = findViewById(R.id.buttonPredict)
        val buttoncw: Button = findViewById(R.id.calculatecw)
        coursename.text = courseName

        buttoncw.setOnClickListener {
            val test1 = Test1.text.toString().toFloatOrNull() ?: 0f
            val test2 = Test2.text.toString().toFloatOrNull() ?: 0f
            val assignment1 = assign1.text.toString().toFloatOrNull() ?: 0f
            val assignment2 = assign2.text.toString().toFloatOrNull() ?: 0f

            val weightedAverage = (test1 * 0.1f + test2 * 0.1f + assignment1 * 0.15f + assignment2 * 0.15f).toInt()

            // Do something with the weighted average, such as displaying it
            cw.setText(weightedAverage.toString())
        }

        toolbar.setNavigationOnClickListener {
         val intent = Intent(this, DashboardPage::class.java)
            startActivity(intent)
        }

        predictScore()
    }

    @Throws(IOException::class)
    fun assetFilePath(assetName: String?): String? {
        val file = File(this.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        this.assets.open(assetName!!).use { `is` ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (`is`.read(buffer).also { read = it } != -1) {
                    os.write(buffer, 0, read)
                }
                os.flush()
            }
            return file.absolutePath
        }
    }
    fun generateTensor(shape: LongArray, numbers: List<Int>): Tensor {
        val inputArray = FloatArray(shape[0].toInt() * shape[1].toInt())
        for (i in 0 until shape[0].toLong()) {
            for (j in 0 until shape[1].toLong()) {
                val index = i * shape[1].toInt() + j
                inputArray[index.toInt()] = numbers[i.toInt() * 2 + j.toInt()].toFloat()
            }
        }
        return Tensor.fromBlob(inputArray, shape)
    }



    fun predictScore() {
        val Test1: EditText = findViewById(R.id.Test1)
        val Test2: EditText = findViewById(R.id.Test2)
        val assign1: EditText = findViewById(R.id.assign1)
        val assign2: EditText = findViewById(R.id.assign2)
        val cw: EditText = findViewById(R.id.cw)
        val buttonPredict: Button = findViewById(R.id.buttonPredict)
        val textViewFinalScoreTitle: TextView = findViewById(R.id.textViewFinalScoreTitle)
        val textViewRequiredExamScoreTitle: TextView = findViewById(R.id.textViewRequiredExamScoreTitle)
        val coursename: TextView = findViewById(R.id.coursename)
        val namecourse = coursename.text.toString()
        buttonPredict.setOnClickListener {
            val numbers = listOf(Test1, Test2, assign1, assign2, cw).map {
                if (it.text.isEmpty()) {
                    Toast.makeText(
                        this@DetailedPrediction,
                        "All fields must be supplied",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val number = it.text.toString().toIntOrNull()
                if (number == null || number < 1 || number > 100) {
                    Toast.makeText(
                        this@DetailedPrediction,
                        "Digits must be greater than 0 and less than 100",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                number
            }
            if (numbers.size == 5) {
                Log.d("scores before", "predictScore: " + numbers)
                Log.d("scores before", "predictScore: " + numbers[0])
                // Call the predictScore method from the ModelInference instance
                val prediction = scorePredictor(numbers[0], numbers[1], numbers[2], numbers[3], numbers[4])
                Log.d("scores", "predictScore: " + prediction)
                // Update UI with the prediction
                textViewFinalScoreTitle.text = "Predicted Final Score: $prediction"

                val finalExam =  finalExamPredictor(numbers[0], numbers[1], numbers[2], numbers[3], numbers[4], prediction.toInt())
                val calculatedFE = finalExam *2
                textViewRequiredExamScoreTitle.text = "Required Final Exam Score: $calculatedFE"

                saveToDb(namecourse, numbers[0], numbers[1], numbers[2], numbers[3], numbers[4], prediction.toInt(), calculatedFE.toInt())
            }



        }
    }

    fun saveToDb(
        courseName: String,
        test1Score: Int?,
        test2Score: Int?,
        assignment1Score: Int?,
        assignment2Score: Int?,
        cwScore: Int?,
        finalScore: Int?,
        finalExam: Int?
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val courseName = courseName
        val test1score = test1Score
        val test2Score = test2Score
        val assign1score = assignment1Score
        val assign2score = assignment2Score
        val CWscore = cwScore
        val finalscore = finalScore
        val finalExam = finalExam
        val validCourseName = courseName.replace("/", "-")


        userId?.let { uid ->
            // Create a HashMap to hold the updated profile data
            val coursepredict = hashMapOf(
                "coursename" to validCourseName,
                "test1" to test1score,
                "test2" to test2Score,
                "assign1" to assign1score,
                "assign2" to assign2score,
                "CW" to CWscore,
                "finalscore" to finalscore,
                "finalexam" to finalExam
            )
            firestore.collection("users").document(uid).collection("courses").document(validCourseName)
                .set(coursepredict as Map<String, Any>)
                .addOnSuccessListener {
                    showToast("Score saved for $courseName")
                }
        }
    }
    fun scorePredictor(
        test1Score: Int?,
        test2Score: Int?,
        assignment1Score: Int?,
        assignment2Score: Int?,
        cwScore: Int?
    ): Float {
        return try {
            // Prepare input data tensor
            val inputArray = floatArrayOf(
                test1Score?.toFloat() ?: 0f,
                test2Score?.toFloat() ?: 0f,
                assignment1Score?.toFloat() ?: 0f,
                assignment2Score?.toFloat() ?: 0f,
                cwScore?.toFloat() ?: 0f
            )
            val inputTensor = Tensor.fromBlob(inputArray, longArrayOf(1, 5))
            Log.d("score tensor", "scorePredictor: $inputTensor")

            // Perform inference
            val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
            Log.d("output tensor", "scorePredictor: $outputTensor")

            // Get prediction
            outputTensor.dataAsFloatArray[0]
        } catch (e: Exception) {
            e.printStackTrace()
            Float.NaN // Return NaN for error
        }
    }
    fun finalExamPredictor(
        test1Score: Int?,
        test2Score: Int?,
        assignment1Score: Int?,
        assignment2Score: Int?,
        cwScore: Int?,
        finalScore: Int?

    ): Float {
        return try {
            // Prepare input data tensor
            val inputArray = floatArrayOf(
                test1Score?.toFloat() ?: 0f,
                test2Score?.toFloat() ?: 0f,
                assignment1Score?.toFloat() ?: 0f,
                assignment2Score?.toFloat() ?: 0f,
                cwScore?.toFloat() ?: 0f,
                finalScore?.toFloat() ?: 0f
            )
            val inputTensor = Tensor.fromBlob(inputArray, longArrayOf(1, 6))
            Log.d("score tensor", "scorePredictor: $inputTensor")

            // Perform inference
            val outputTensor = module2.forward(IValue.from(inputTensor)).toTensor()
            Log.d("output tensor", "scorePredictor: $outputTensor")

            // Get prediction
            outputTensor.dataAsFloatArray[0]
        } catch (e: Exception) {
            e.printStackTrace()
            Float.NaN // Return NaN for error
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    }






