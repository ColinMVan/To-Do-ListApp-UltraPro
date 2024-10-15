package com.example.to_do_listapp

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskEditText: EditText
    private lateinit var saveTaskButton: Button
    private lateinit var cancelTaskButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_task)

        taskEditText = findViewById(R.id.taskEditText)
        saveTaskButton = findViewById(R.id.saveTaskButton)
        cancelTaskButton = findViewById(R.id.cancelTaskButton)

        // Set OnClickListener for Save button
        saveTaskButton.setOnClickListener {
            val taskText = taskEditText.text.toString().trim()

            // Validate input must be at least 3 characters
            if (TextUtils.isEmpty(taskText) || taskText.length < 3) {
                Toast.makeText(this, "Please enter at least 3 characters.", Toast.LENGTH_SHORT).show()
            } else {
                // Task is valid then it is saved the task
                saveTask(taskText)
            }
        }

        // Set OnClickListener for Cancel button
        cancelTaskButton.setOnClickListener {
            finish() // Close the activity and go back to the main screen
        }
    }

    // Method to save the task (for demonstration purposes)
    private fun saveTask(taskText: String) {
        // Here you can add the logic to save the task to your data source (  database or a shared preference)
        Toast.makeText(this, "Task '$taskText' saved successfully!", Toast.LENGTH_SHORT).show()
        finish() // Close the activity after saving the task
    }
}