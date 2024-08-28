# Students Performance Tracking App - Android Application

## Overview

This Android application leverages machine learning to predict the final grades of students based on their assessment scores throughout the semester. The application uses the **Random Forest Regressor** algorithm to model the relationship between assessment scores and final grades, providing accurate predictions to help educators and students alike.

## Features

- **Student Data Input**: Users can input individual assessment scores for each student.
- **Grade Prediction**: The app predicts the final grade for each student based on their inputted assessment scores.
- **User-Friendly Interface**: A clean and intuitive interface for seamless interaction.
- **Offline Capability**: Perform predictions without the need for an active internet connection.

## Machine Learning Model

The core of this application is the **Random Forest Regressor** algorithm, which is a powerful ensemble method that combines multiple decision trees to improve prediction accuracy. The model is trained using historical student data, making it capable of providing reliable grade predictions.

### Why Random Forest?

- **Accuracy**: Random Forest is known for its high accuracy and robustness.
- **Flexibility**: It handles both numerical and categorical data well.
- **Overfitting Prevention**: By averaging multiple trees, it reduces the risk of overfitting.

## Technology Stack

- **Frontend**: Android (Kotlin)
- **Backend**: Firebase (for user management and data storage)
- **Machine Learning**: Random Forest Regressor (implemented in Python/Scikit-learn and integrated with the app)

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/rizkihelmi10/studentpredictionapp_git.git
   ```
2. **Open the project** in Android Studio.
3. **Set up Firebase**: Follow the instructions to integrate your Firebase project with the app.
4. **Build and run** the application on an Android device or emulator.

## Usage

1. **Add Student Data**: Input the student's assessment scores throughout the semester.
2. **Predict Grades**: Tap the "Predict" button to calculate the student's final grade.
3. **View Results**: The predicted final grade will be displayed instantly.

## Contributing

Contributions are welcome! Please fork this repository and submit a pull request for any improvements or new features. Make sure to follow the coding guidelines and document your changes.


## Contact

For any inquiries or support, please contact [your.email@example.com](mailto:rizkihelmi1008@gmail.com).

---
