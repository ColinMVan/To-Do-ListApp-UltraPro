package com.example.to_do_listapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
   private lateinit var addButton: Button
   private lateinit var aboutButton: Button
   private lateinit var recyclerView: RecyclerView
   private lateinit var tooMuchWorkText: TextView

   private val tasks = mutableListOf<String>() // Task list
   private lateinit var taskAdapter: TaskAdapter

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

      // Initialize views
      addButton = findViewById(R.id.Addbutton)
      aboutButton = findViewById(R.id.Aboutbutton)
      recyclerView = findViewById(R.id.recyclerView)
      tooMuchWorkText = findViewById(R.id.textViewTooMuchWork) // Ensure this TextView is in your layout

      // RecyclerView setup
      taskAdapter = TaskAdapter(tasks)
      recyclerView.adapter = taskAdapter
      recyclerView.layoutManager = LinearLayoutManager(this)

      // Add Button
      addButton.setOnClickListener {
         if (tasks.size < 7) {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, 1)
         }
      }

      // About Button
      aboutButton.setOnClickListener {
         val intent = Intent(this, AboutActivity::class.java)
         startActivity(intent)
      }

      // Initially check if "Too much work!" message should be shown
      updateAddButtonVisibility()
   }

   // Method to toggle the visibility of Add Button and "Too much work!" message
   private fun updateAddButtonVisibility() {
      if (tasks.size >= 7) {
         addButton.visibility = View.GONE
         tooMuchWorkText.visibility = View.VISIBLE
      } else {
         addButton.visibility = View.VISIBLE
         tooMuchWorkText.visibility = View.GONE
      }
   }

   // This method handles results from AddTaskActivity and adds the new task
   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == 1 && resultCode == RESULT_OK) {
         val newTask = data?.getStringExtra("task")
         if (newTask != null) {
            tasks.add(newTask)
            taskAdapter.notifyDataSetChanged()
            updateAddButtonVisibility() // Update visibility when a new task is added
         }
      }
   }
}