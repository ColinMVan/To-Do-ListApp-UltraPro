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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_details)

        taskTextView = findViewById(R.id.taskNameTextView)
        markCompleteButton = findViewById(R.id.markCompleteButton)
        cancelButton = findViewById(R.id.cancelButton)
        previousTaskButton = findViewById(R.id.previousTaskButton)
        nextTaskButton = findViewById(R.id.nextTaskButton)

        val task = intent.getStringExtra("task")
        taskTextView.text = task

        // Implement buttons for task navigation and completion
        markCompleteButton.setOnClickListener {
            // Complete the task logic
            finish() // Close after completion
        }

        cancelButton.setOnClickListener {
            finish() // Close without changes
        }

        // Logic for previous and next task navigation can be added here
    }
}