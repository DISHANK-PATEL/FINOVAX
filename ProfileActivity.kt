package com.example.finanaceapp.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.finanaceapp.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var openGalleryBtn: Button
    private lateinit var toolbar: Toolbar
    private lateinit var profileName: EditText
    private lateinit var profileEmail: EditText
    private lateinit var saveButton: Button
    private var selectedImageUri: Uri? = null


    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            profileImage.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        init()

        toolbar.setNavigationOnClickListener {
            finish()
        }

        openGalleryBtn.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        saveButton.setOnClickListener {
            val name = profileName.text.toString()
            val email = profileEmail.text.toString()
            val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
            sharedPreferences.edit()
                .putString("name", name)
                .putString("email", email)
                .putString("image_uri", selectedImageUri?.toString())
                .apply()
            finish()
        }
    }

    /*override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("UserProfile", MODE_PRIVATE)
        profileName.setText(prefs.getString("name", ""))
        profileEmail.setText(prefs.getString("email", ""))
        prefs.getString("image_uri", null)?.let {
            selectedImageUri = Uri.parse(it)
            profileImage.setImageURI(selectedImageUri)
        }
    }*/

    private fun init() {
        profileImage = findViewById(R.id.profile_image)
        openGalleryBtn = findViewById(R.id.btn_open_gallary)
        toolbar = findViewById(R.id.toolbar)
        profileName = findViewById(R.id.name_profile)
        profileEmail = findViewById(R.id.profile_email)
        saveButton = findViewById(R.id.btn_save)
        setSupportActionBar(toolbar)
    }
}
