package com.example.finanaceapp.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finanaceapp.Adapter.ExpenseListAdapter
import com.example.finanaceapp.R
import com.example.finanaceapp.ViewModel.MainViewModel
import com.example.finanaceapp.databinding.ActivityMainBinding
import eightbitlab.com.blurview.RenderScriptBlur

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var expenseListAdapter: ExpenseListAdapter
    private val mainViewModel by viewModels<MainViewModel>()

    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        checkSmsPermission()
     //   loadUserProfile()
        initRecyclerview()
        setBlueEffect()
        setClickListeners()
    }

    private fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            smsPermissionLauncher.launch(Manifest.permission.READ_SMS)
        }
    }

    private fun setClickListeners() {
        binding.addTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        binding.messageadd.setOnClickListener {
            startActivity(Intent(this, MessageActivity::class.java))
        }

        binding.cardBtn.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun initRecyclerview() {
        binding.recyclerView1.layoutManager = LinearLayoutManager(this)
        expenseListAdapter = ExpenseListAdapter(emptyList())
        binding.recyclerView1.adapter = expenseListAdapter
        binding.recyclerView1.isNestedScrollingEnabled = false

        mainViewModel.allTransactions.observe(this) { transactions ->
            expenseListAdapter.updateList(transactions)
        }
    }

    private fun setBlueEffect() {
        val radius = 10f
        val decorView = this.window.decorView
        val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background

        binding.blueView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)

        binding.blueView.setOutlineProvider(ViewOutlineProvider.BACKGROUND)
        binding.blueView.setClipToOutline(true)
    }

    private fun loadUserProfile() {
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")
        val email = sharedPreferences.getString("email", "")
        val imageUriString = sharedPreferences.getString("image_uri", null)

        binding.nameProfile.text = name ?: "No Name"
        binding.emailProfile.text = email ?: "No Email"

        if (!imageUriString.isNullOrEmpty()) {
            val imageUri = Uri.parse(imageUriString)
            binding.imageProfile.setImageURI(imageUri)
        }
    }
}
