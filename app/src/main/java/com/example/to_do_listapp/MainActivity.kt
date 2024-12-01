package com.example.to_do_listapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

data class Task(val taskName: String = "")

class MainActivity : AppCompatActivity() {

   private val signInLauncher = registerForActivityResult(
      FirebaseAuthUIActivityResultContract()
   ) { res ->
      this.onSignInResult(res)
   }

   private lateinit var userName: TextView
   private lateinit var addButton: Button
   private lateinit var signOutButton: Button
   private lateinit var recyclerView: RecyclerView
   private lateinit var taskAdapter: TaskAdapter

   private val tasks: MutableList<String> = mutableListOf() // Task list
   private lateinit var firestore: FirebaseFirestore
   private var userId: String? = null // Current user's unique ID

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

      // Initialize Firebase Firestore
      firestore = FirebaseFirestore.getInstance()

      // Initialize views
      userName = findViewById(R.id.userName)
      addButton = findViewById(R.id.Addbutton)
      signOutButton = findViewById(R.id.signOutButton)
      recyclerView = findViewById(R.id.recyclerView)

      // RecyclerView setup
      taskAdapter = TaskAdapter(tasks)
      recyclerView.adapter = taskAdapter
      recyclerView.layoutManager = LinearLayoutManager(this)

      // Add Button
      addButton.setOnClickListener {
         if (tasks.size < 7) {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putStringArrayListExtra("taskList", ArrayList(tasks))
            startActivityForResult(intent, 1)
         }
      }

      // Sign Out Button
      signOutButton.setOnClickListener {
         signOutUser()
      }

      // Check if user is already signed in
      val currentUser = FirebaseAuth.getInstance().currentUser
      if (currentUser == null) {
         initiateSignIn()
      } else {
         userId = currentUser.uid
         updateUI(currentUser)
         fetchUserTasks() // Fetch user's tasks on login
      }
   }

   private fun initiateSignIn() {
      val providers = arrayListOf(
         AuthUI.IdpConfig.EmailBuilder().build(),
         AuthUI.IdpConfig.GoogleBuilder().build()
      )

      val signInIntent = AuthUI.getInstance()
         .createSignInIntentBuilder()
         .setAvailableProviders(providers)
         .setIsSmartLockEnabled(false)
         .build()
      signInLauncher.launch(signInIntent)
   }

   private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
      val response = result.idpResponse
      if (result.resultCode == RESULT_OK) {
         // Successfully signed in
         val user = FirebaseAuth.getInstance().currentUser
         user?.let {
            userId = it.uid
            updateUI(it)
            fetchUserTasks()
         }
      } else {
         Log.i("Auth", "Sign-in failed: ${response?.error?.message}")
         Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
      }
   }

   private fun updateUI(user: FirebaseUser) {
      userName.text = "Welcome, ${user.displayName ?: "User"}"
      Toast.makeText(this, "Signed in as ${user.email}", Toast.LENGTH_SHORT).show()
   }

   private fun signOutUser() {
      AuthUI.getInstance()
         .signOut(this)
         .addOnCompleteListener {
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            tasks.clear() // Clear local tasks
            taskAdapter.notifyDataSetChanged()
            initiateSignIn() // Redirect back to sign-in screen
         }
   }

   private fun fetchUserTasks() {
      if (userId == null) return

      firestore.collection("users")
         .document(userId!!)
         .collection("tasks")
         .get()
         .addOnSuccessListener { querySnapshot ->
            tasks.clear()
            for (doc in querySnapshot) {
               val task = doc.toObject<Task>()
               tasks.add(task.taskName)
            }
            taskAdapter.notifyDataSetChanged()
         }
         .addOnFailureListener {
            Log.e("Firestore", "Failed to fetch tasks", it)
            Toast.makeText(this, "Failed to fetch tasks", Toast.LENGTH_SHORT).show()
         }
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == 1 && resultCode == RESULT_OK) {
         val newTask = data?.getStringExtra("task")
         if (newTask != null) {
            tasks.add(newTask)
            taskAdapter.notifyDataSetChanged()

            // Save task to Firestore
            saveTaskToFirestore(newTask)
         }
      }
   }

   private fun saveTaskToFirestore(taskName: String) {
      if (userId == null) return

      val task = hashMapOf("taskName" to taskName)
      firestore.collection("users")
         .document(userId!!)
         .collection("tasks")
         .add(task)
         .addOnSuccessListener {
            Log.d("Firestore", "Task added successfully")
         }
         .addOnFailureListener {
            Log.e("Firestore", "Failed to add task", it)
         }
   }
}