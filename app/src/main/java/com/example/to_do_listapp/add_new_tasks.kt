package com.example.to_do_listapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {
    private lateinit var taskEditText: EditText
    private lateinit var saveTaskButton: Button
    private lateinit var cancelTaskButton: Button
    private lateinit var charLimitTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_task)

        taskEditText = findViewById(R.id.taskEditText)
        saveTaskButton = findViewById(R.id.saveTaskButton)
        cancelTaskButton = findViewById(R.id.cancelTaskButton)
        charLimitTextView = findViewById(R.id.charLimitTextView)

        taskEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val charCount = s?.length ?: 0
                charLimitTextView.text = "$charCount/25 characters"
                //Character min and max
                saveTaskButton.isEnabled = charCount in 3..25
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        // Saving the task button
        saveTaskButton.setOnClickListener {
            val taskText = taskEditText.text.toString()
            if (!taskText.isEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("task", taskText)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
        // Canceling the task button
        cancelTaskButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}