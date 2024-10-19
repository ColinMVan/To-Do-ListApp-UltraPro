package com.example.to_do_listapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    private lateinit var backButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_app)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

}