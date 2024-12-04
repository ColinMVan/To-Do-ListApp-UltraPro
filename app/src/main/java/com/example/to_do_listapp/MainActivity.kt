package com.example.to_do_listapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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
   //User interface elements
   private lateinit var userName: TextView
   private lateinit var addButton: Button
   private lateinit var signOutButton: Button
   private lateinit var recyclerView: RecyclerView
   private lateinit var searchButton: Button
   private lateinit var searchBox: EditText
   private lateinit var taskAdapter: TaskAdapter
   private lateinit var signInButton: Button
   private lateinit var loginInButton: Button
   private lateinit var emailField: EditText
   private lateinit var passwordField: EditText


   private val tasks: MutableList<String> = mutableListOf() // Task list
   private val allTasks: MutableList<String> = mutableListOf()
   private lateinit var firestore: FirebaseFirestore
   private var userId: String? = null // Current user's unique ID

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

      // Initialize Firebase Firestore
      firestore = FirebaseFirestore.getInstance()

      // Initialize user views
      userName = findViewById(R.id.userName)
      addButton = findViewById(R.id.Addbutton)
      signOutButton = findViewById(R.id.signOutButton)
      recyclerView = findViewById(R.id.recyclerView)
      searchButton = findViewById(R.id.searchButton)
      searchBox = findViewById(R.id.searchBox)
      signInButton = findViewById(R.id.signInButton1)
      loginInButton = findViewById(R.id.loginButton)
      emailField = findViewById(R.id.emailField)
      passwordField = findViewById(R.id.passwordField)


      // RecyclerView setup
      taskAdapter = TaskAdapter(tasks)
      recyclerView.adapter = taskAdapter
      recyclerView.layoutManager = LinearLayoutManager(this)

      // Add Button
      addButton.setOnClickListener {
         val newTask = searchBox.text.toString().trim()
         if (newTask.isNotEmpty() && tasks.size < 7) {
            tasks.add(newTask)
            allTasks.add(newTask)
            taskAdapter.notifyDataSetChanged()
            saveTaskToFirestore(newTask)
         } else {
            Toast.makeText(this, "Task limit reached or invalid input", Toast.LENGTH_SHORT).show()
         }
      }

      searchButton.setOnClickListener {
         val query = searchBox.text.toString().trim()
         filterTasks(query)
      }
      // Sign In Button
      signInButton.setOnClickListener {
         initiateSignIn()
      }

      // Sign Out Button
      signOutButton.setOnClickListener {
         signOutUser()
      }
      // Login Button
      loginInButton.setOnClickListener {
         val email = emailField.text.toString().trim()
         val password = passwordField.text.toString().trim()

         if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
         }
         // Authenticating the user for logging back in
         FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
               if (task.isSuccessful) {
                  val user = FirebaseAuth.getInstance().currentUser
                  if (user != null) {
                     userId = user.uid
                     updateUI(user)
                     fetchUserTasks()
                     passwordField.requestFocus()
                     Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                  }
               } else {
                  Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                  Log.e("Auth", "Login failed", task.exception)
               }
            }
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

   //The filterTasks function updates the list of tasks displayed on the UI based on the user's search query.
   private fun filterTasks(query: String) {
      tasks.clear()
      if (query.isEmpty()) {
         tasks.addAll(allTasks)
      } else {
         tasks.addAll(allTasks.filter { it == query })
      }
      taskAdapter.notifyDataSetChanged()
      if (tasks.isEmpty()) {
         Toast.makeText(this, "No tasks match \"$query\"", Toast.LENGTH_SHORT).show()
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
   // The function handles the result of a Firebase Authentication sign-in process
   private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
      val response = result.idpResponse
      if (result.resultCode == RESULT_OK) {
         // Successfully signed in
         val user = FirebaseAuth.getInstance().currentUser
         if (user != null) {
            Log.d("Auth", "Sign-in successful: ${user.uid}")
            userId = user.uid
            updateUI(user)
            fetchUserTasks()
         } else {
            Log.i("Auth", "Sign-in failed: User object is null")
            initiateSignIn()
         }
      } else {
         Log.i("Auth", "Sign-in failed: ${response?.error?.message}")
         Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
      }
   }
   // Updating the UI for certain visability options when user is logged in or logged out
   private fun updateUI(user: FirebaseUser) {
      userName.text = "Welcome, ${user.displayName ?: "User"}"
      signInButton.visibility = View.GONE
      signOutButton.visibility = View.VISIBLE
      loginInButton.visibility = View.GONE
      Toast.makeText(this, "Signed in as ${user.email}", Toast.LENGTH_SHORT).show()
      fetchUserTasks()
   }
   // Signs the user out and provide a toast message when user is logged out
   private fun signOutUser() {
      AuthUI.getInstance()
         .signOut(this)
         .addOnCompleteListener {
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            tasks.clear() // Clear local tasks
            taskAdapter.notifyDataSetChanged()
            userId = null
            userName.text = ""
         }
   }
      // Grab the user task for them and give toast message if failure to do such action
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
   // This save the task to the Firestore for the user
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
   // Signs the  user out if the app goes into the background
   override fun onStop() {
      super.onStop()
      val currentUser = FirebaseAuth.getInstance().currentUser
      if (currentUser != null) {
         Log.d("Auth", "App went into the background, user is still logged in.")
      } else{
         signOutUser()
      }
   }

   // Signs the User out the app is paused
   override fun onPause() {
      super.onPause()
      signOutUser()

   }
   // The signInButton appears on application opening
   override fun onStart() {
      super.onStart()
      val signInButton: Button = findViewById(R.id.signInButton1)
      val currentUser = FirebaseAuth.getInstance().currentUser

      if (currentUser == null) {
         // If the person isn't signed in the sign in Button shall be shown
         signInButton.visibility = View.VISIBLE
      } else {
         // If the user is signed in the Button won't be shown
         signInButton.visibility = View.GONE
         userId = currentUser.uid
         updateUI(currentUser)
         fetchUserTasks()
      }
   }
   // Show the signout button when the user is logged in
   private fun signOutUserOnInvisibility() {
      AuthUI.getInstance()
         .signOut(this)
         .addOnCompleteListener {
            Log.d("Auth", "User signed out as app became invisible.")
            tasks.clear() // Clear local tasks
            taskAdapter.notifyDataSetChanged()
            initiateSignIn() // Redirect to sign-in screen
         }
   }
}