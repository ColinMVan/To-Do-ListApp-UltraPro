package com.example.to_do_listapp

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
    private lateinit var tasks: List<String> // List to store tasks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_details)

        taskTextView = findViewById(R.id.taskNameTextView)
        markCompleteButton = findViewById(R.id.markCompleteButton)
        cancelButton = findViewById(R.id.cancelButton)
        previousTaskButton = findViewById(R.id.previousTaskButton)
        nextTaskButton = findViewById(R.id.nextTaskButton)

        // Simulating a list of tasks for navigation
        tasks = listOf("Task 1", "Task 2", "Task 3", "Task 4")

        // Get task and task index from the intent
        val taskIndex = intent.getIntExtra("taskIndex", 0)
        taskTextView.text = tasks[taskIndex]

        // Complete task logic
        markCompleteButton.setOnClickListener {
            finish() // Close activity
        }

        cancelButton.setOnClickListener {
            finish() // Close activity without changes
        }

        // Previous Task button logic
        previousTaskButton.setOnClickListener {
            val currentTaskIndex = intent.getIntExtra("taskIndex", 0)
            if (currentTaskIndex > 0) {
                val previousTask = tasks[currentTaskIndex - 1]
                taskTextView.text = previousTask
                intent.putExtra("taskIndex", currentTaskIndex - 1)
            }
        }

        // Next Task button logic
        nextTaskButton.setOnClickListener {
            val currentTaskIndex = intent.getIntExtra("taskIndex", 0)
            if (currentTaskIndex < tasks.size - 1) {
                val nextTask = tasks[currentTaskIndex + 1]
                taskTextView.text = nextTask
                intent.putExtra("taskIndex", currentTaskIndex + 1)
            }
        }
    }
}