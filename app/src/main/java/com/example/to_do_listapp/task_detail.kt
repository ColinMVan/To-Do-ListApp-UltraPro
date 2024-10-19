package com.example.to_do_listapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class task_detail : AppCompatActivity() {
    private lateinit var taskTextView: TextView
    private lateinit var markCompleteButton: Button
    private lateinit var cancelButton: Button
    private lateinit var previousTaskButton: Button
    private lateinit var nextTaskButton: Button
    private var taskIndex: Int = -1
    private lateinit var tasks: List<String> // Declare the tasks list here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_details)

        taskTextView = findViewById(R.id.taskNameTextView)
        markCompleteButton = findViewById(R.id.markCompleteButton)
        cancelButton = findViewById(R.id.cancelButton)
        previousTaskButton = findViewById(R.id.previousTaskButton)
        nextTaskButton = findViewById(R.id.nextTaskButton)

        // Retrieve the task and task index from the intent
        val task = intent.getStringExtra("task")
        taskIndex = intent.getIntExtra("taskIndex", -1)
        tasks = intent.getStringArrayListExtra("taskList") ?: emptyList() // Retrieve the tasks list

        // Display the current task in the TextView
        taskTextView.text = task ?: "No task found"

        // Mark task as complete logic
        markCompleteButton.setOnClickListener {
            if (taskIndex != -1) {
                val resultIntent = Intent()
                resultIntent.putExtra("taskIndex", taskIndex)
                setResult(RESULT_OK, resultIntent)
                finish() // Close activity
            }
        }

        // Previous Task Button
        previousTaskButton.setOnClickListener {
            if (taskIndex > 0) {
                taskIndex-- // Decrease task index
                updateTaskDisplay()
            }
        }

        // Next Task Button
        nextTaskButton.setOnClickListener {
            if (taskIndex < tasks.size - 1) {
                taskIndex++ // Increase task index
                updateTaskDisplay()
            }
        }

        // Cancel button closes the activity without any action
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun updateTaskDisplay() {
        taskTextView.text = tasks[taskIndex]
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("taskIndex", taskIndex) // Save the task index
    }
}