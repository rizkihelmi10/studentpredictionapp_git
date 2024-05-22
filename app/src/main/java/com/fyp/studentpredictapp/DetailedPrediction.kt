package com.fyp.studentpredictapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
import kotlin.time.times


class DetailedPrediction : AppCompatActivity() {
    lateinit var module: Module
    lateinit var module2: Module
    private lateinit var firestore: FirebaseFirestore
    private lateinit var courseName: String
    private lateinit var  courseNameTake: String
    private lateinit var percentage: String
    private lateinit var cwtext: EditText
    private lateinit var assessmentType: String
    private lateinit var textViewFinalScoreTitle: TextView
    private lateinit var textViewRequiredExamScoreTitle: TextView
    private lateinit var  buttonPredict: Button
    private lateinit var finalexam: TextView
    private lateinit var finalscore: TextView
    private lateinit var buttonCalculateCoursework: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_prediction)
        courseName = intent.getStringExtra("selected_course").toString()
        courseNameTake = intent.getStringExtra("selected_course_name").toString()
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
        retrieveDataAndCreateViews()
        /*val textViewTitle: TextView = findViewById(R.id.textViewTitle)
        val textViewDescription: TextView = findViewById(R.id.textViewDescription)*/
        val coursename: TextView = findViewById(R.id.coursename)
        textViewFinalScoreTitle= findViewById(R.id.textViewFinalScoreTitle)
        textViewRequiredExamScoreTitle = findViewById(R.id.textViewRequiredExamScoreTitle)
        finalexam = findViewById(R.id.finalexam)
        finalscore = findViewById(R.id.finalscore)
//        val Test1tx: TextView = findViewById(R.id.Test1tx)
//        val Test2tx: TextView = findViewById(R.id.Test2tx)
//        val assign1tx: TextView = findViewById(R.id.assign1tx)
//        val assign2tx: TextView = findViewById(R.id.assign2tx)
//        cwtext = findViewById(R.id.cwtx)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbarpred)


//        val Test1: EditText = findViewById(R.id.Test1)
//        val Test2: EditText = findViewById(R.id.Test2)
//        val assign1: EditText = findViewById(R.id.assign1)
//        val assign2: EditText = findViewById(R.id.assign2)
        cwtext = findViewById(R.id.cw)

        buttonPredict = findViewById(R.id.buttonPredict)
        val buttoncw: Button = findViewById(R.id.calculatecw)
        coursename.text = courseNameTake

        buttoncw.setOnClickListener {
           /* val test1 = Test1.text.toString().toFloatOrNull() ?: 0f
            val test2 = Test2.text.toString().toFloatOrNull() ?: 0f
            val assignment1 = assign1.text.toString().toFloatOrNull() ?: 0f
            val assignment2 = assign2.text.toString().toFloatOrNull() ?: 0f

            val weightedAverage = (test1 * 0.1f + test2 * 0.1f + assignment1 * 0.15f + assignment2 * 0.15f).toInt()

            // Do something with the weighted average, such as displaying it
            cw.setText(weightedAverage.toString())*/
        }

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, PredictScore::class.java)
            startActivity(intent)
        }

        //predictScore()
    }
    private fun retrieveDataAndCreateViews() {
        // Check if courseName is not empty
        if (courseName.isNotEmpty()) {
            // Retrieve the current user's ID
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId == null) {
                Log.d("DetailedPrediction", "User ID is null")
                return
            }

            Log.d("DetailedPrediction", "User ID: $userId")
            Log.d("DetailedPrediction", "Course Code: $courseName")

            // Query Firestore to get the specific course document based on the provided path
            firestore.collection("users")
                .document(userId)
                .collection("courses")
                .whereEqualTo("courseCode", courseName)
                .limit(1) // Assuming courseCode is unique, we can limit to one result
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result
                        if (documents != null && !documents.isEmpty) {
                            val courseDocument = documents.documents[0] // Get the first (and only) document

                            // Log the course document ID for debugging purposes
                            Log.d("DetailedPrediction", "Course Document ID: ${courseDocument.id}")

                            // Now get the assessments collection under this course document
                            courseDocument.reference.collection("assessments")
                                .document("all_assessments")
                                .get()
                                .addOnCompleteListener { assessmentTask ->
                                    if (assessmentTask.isSuccessful) {
                                        val assessmentDocument = assessmentTask.result
                                        if (assessmentDocument != null && assessmentDocument.exists()) {
                                            val assessments = assessmentDocument.get("assessments") as? List<Map<String, Any>>

                                            // Dynamically create views based on assessments
                                            assessments?.let {
                                                createDynamicViews(it)
                                            } ?: run {
                                                Log.d("DetailedPrediction", "No assessments found")
                                            }
                                        } else {
                                            Log.d("DetailedPrediction", "Assessment document does not exist")
                                        }
                                    } else {
                                        Log.d("DetailedPrediction", "Failed to get assessments: ", assessmentTask.exception)
                                    }
                                }
                        } else {
                            Log.d("Document", "No such document")
                            Log.d("DetailedPrediction", "Query snapshot is empty")
                        }
                    } else {
                        Log.d("Document", "get failed with ", task.exception)
                    }
                }
        } else {
            Log.d("DetailedPrediction", "Course code is empty")
        }
    }



    private fun createDynamicViews(assessments: List<Map<String, Any>>) {
        val dynamicViewsContainer = findViewById<LinearLayout>(R.id.dynamicViewsContainer) // ID of your dynamic views container

        for (assessment in assessments) {
            assessmentType = assessment["assessmentType"] as? String ?: ""
            percentage = assessment["percentage"] as? String ?: ""

            // Create TextView for assessment type
            val textView = TextView(this).apply {
                text = assessmentType
                textSize = 18f
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 16, 0, 0)
                layoutParams = params
            }

            // Create EditText for assessment score
            val editText = EditText(this).apply {
                inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                hint = "Score"
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 8, 0, 16)
                layoutParams = params
            }

            // Add TextView and EditText to dynamic views container
            dynamicViewsContainer.addView(textView)
            dynamicViewsContainer.addView(editText)

            // Calculate total score for each assessment
            buttonCalculateCoursework = findViewById(R.id.calculatecw)
            buttonCalculateCoursework.setOnClickListener {
                calculateCoursework()
            }
        }
    }
    private fun calculateCoursework() {
        val dynamicViewsContainer = findViewById<LinearLayout>(R.id.dynamicViewsContainer)
        val totalAssessments = dynamicViewsContainer.childCount / 2
        val assessments = mutableListOf<Pair<String, Double>>()

        var totalCourseworkScore = 0.0
        var totalPercentage = 0.0
        var score = 0.0

        // Collect assessment names and percentages from dynamic views
        for (i in 0 until dynamicViewsContainer.childCount step 2) {
            val editText = dynamicViewsContainer.getChildAt(i + 1) as? EditText
            val textView = dynamicViewsContainer.getChildAt(i) as? TextView

             score = editText?.text.toString().toDoubleOrNull() ?: 0.0


            val percentage = percentage.replace("%", "").toDoubleOrNull() ?: 0.0
            totalPercentage += percentage

            assessments.add(Pair("Assessment ${assessments.size + 1}" + textView, percentage))

            val percentagenew = percentage / 100.0
            val coursework = score * percentagenew
            totalCourseworkScore += coursework
        }

        // Generate the numbers array
        val numbers = createNumbersArray(assessments, totalCourseworkScore, totalPercentage)

        // Log or handle the generated numbers array
        Log.d("DetailedPrediction", "Generated Numbers Array: ${numbers.joinToString()}")

        // Display total coursework score
        cwtext.setText(totalCourseworkScore.toString())
        if(cwtext.text.isNotEmpty()){
            buttonCalculateCoursework.visibility = View.INVISIBLE
            buttonPredict.visibility = View.VISIBLE

        }

        // Display total percentage
        Log.d("DetailedPrediction", "Total Percentage: $totalPercentage")
    }

    private fun createNumbersArray(assessments: List<Pair<String, Double>>, coursework: Double, totalpercent: Double): DoubleArray {
        val numbers = DoubleArray(5) // Initialize the array with 5 elements

        // Sort the assessments by percentage in descending order
        val sortedAssessments = assessments.sortedByDescending { it.second }
        Log.d("DetailedPrediction", "sorted? $sortedAssessments")
        // Initialize current percentage
        var currentPercentage = sortedAssessments.firstOrNull()?.second ?: 0.0
        Log.d("DetailedPrediction", "sorted? $currentPercentage")
        // Iterate through each assessment and fill the numbers array accordingly
        var remainingPercentage = totalpercent

        for (i in 0 until numbers.size - 1) {
            when (i) {
                0, 1 -> {
                    numbers[i] = coursework * 0.1
                    remainingPercentage -= 10
                }
                2, 3 -> {
                    numbers[i] = coursework * 0.15
                    remainingPercentage -= 15
                }
            }

            // Log the remaining percentage for debugging
            Log.d("DetailedPrediction", "remainingPercentage after iteration $i: $remainingPercentage")

            // Update currentPercentage for the next assessment
            if (i < sortedAssessments.size - 1) {
                val nextPercentage = sortedAssessments[i + 1].second
                currentPercentage = nextPercentage
                Log.d("DetailedPrediction", "Updated currentPercentage for next assessment: $currentPercentage")
            }
        }

        // Assign coursework to the last element of the numbers array
        numbers[numbers.size - 1] = coursework
        buttonPredict.setOnClickListener {
            finalexam.visibility = View.VISIBLE
            finalscore.visibility = View.VISIBLE
            val prediction = scorePredictor(numbers[0].toInt(), numbers[1].toInt(), numbers[2].toInt(), numbers[3].toInt(), numbers[4].toInt())
            finalscore.text = String.format("%.2f",prediction)
            val finalExam =  finalExamPredictor(numbers[0].toInt(), numbers[1].toInt(), numbers[2].toInt(), numbers[3].toInt(), numbers[4].toInt(), prediction.toInt())
            val multiplier = when (totalpercent) {
                50.0 -> 2
                60.0 -> 1.67
                40.0 -> 2.5
                else -> 2.0 // default multiplier if totalpercent is not 50 or 60
            }
            val calculatedFE = finalExam.toDouble() * multiplier.toDouble()

            finalexam.text = String.format("%.2f", calculatedFE)
            savetoDb(assessments,prediction.toDouble(),calculatedFE)
        }



        return numbers
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
private fun savetoDb(assessments: List<Pair<String, Double>>, predictedFinalScore: Double, predictedFinalExam: Double) {
    val dynamicViewsContainer = findViewById<LinearLayout>(R.id.dynamicViewsContainer)
    val totalAssessments = dynamicViewsContainer.childCount / 2
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null) {
        val assessments = mutableListOf<Map<String, Any>>()
        var totalCourseworkScore = 0.0
        var totalPercentage = 0.0
        var score = 0.0

        // Collect assessment names and percentages from dynamic views
        for (i in 0 until dynamicViewsContainer.childCount step 2) {
            val editText = dynamicViewsContainer.getChildAt(i + 1) as? EditText
            val textView = dynamicViewsContainer.getChildAt(i) as? TextView

            score = editText?.text.toString().toDoubleOrNull() ?: 0.0


            val percentage = percentage.replace("%", "").toDoubleOrNull() ?: 0.0
            if (textView != null) {
                assessments.add(mapOf(
                    "assessmentType" to  textView.text,
                    "percentage" to percentage,
                    "score" to score
                ))
            }
            totalPercentage += percentage

            val percentagenew = percentage / 100.0
            val coursework = score * percentagenew
            totalCourseworkScore += coursework
        }
        val courseRef = firestore.collection("users")
            .document(userId)
            .collection("courses")
            .whereEqualTo("courseCode", courseName)
            .limit(1)  // Assuming courseCode is unique, we limit to one result

        courseRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documents = task.result
                if (documents != null && !documents.isEmpty) {
                    val document = documents.documents[0] // Get the first (and only) document
                    val documentId = document.id

                    // Now update the specific document
                    val updateData = mapOf(
                        "assessments" to assessments,
                        "coursework" to totalCourseworkScore,
                        "predictedScore" to predictedFinalScore,
                        "predictedFinal" to predictedFinalExam
                    )

                    firestore.collection("users")
                        .document(userId)
                        .collection("courses")
                        .document(documentId)
                        .update(updateData)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Course data successfully updated!")
                            Toast.makeText(this, "Course Prediction data successfully stored", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error updating document", e)
                        }
                } else {
                    Log.d("Firestore", "No such document")
                }
            } else {
                Log.d("Firestore", "get failed with ", task.exception)
            }
        }
    } else {
        Log.d("Firestore", "User ID is null")
    }

}


    /*fun predictScore() {
        *//*val Test1: EditText = findViewById(R.id.Test1)
        val Test2: EditText = findViewById(R.id.Test2)
        val assign1: EditText = findViewById(R.id.assign1)
        val assign2: EditText = findViewById(R.id.assign2)
        val cw: EditText = findViewById(R.id.cw)*//*
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
    }*/

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






