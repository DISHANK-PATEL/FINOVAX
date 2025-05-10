package com.example.finanaceapp.Activity

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finanaceapp.Adapter.ReportListAdapter
import com.example.finanaceapp.R
import com.example.finanaceapp.ViewModel.MainViewModel
import com.example.finanaceapp.databinding.ActivityReportBinding

class ReportActivity : AppCompatActivity() {
    lateinit var binding: ActivityReportBinding
    private val mainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding= ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
      initRecyclerview()
        setVariable()
    }
    fun setVariable(){
        binding.backbtn.setOnClickListener {
            finish()
        }
    }
    fun initRecyclerview(){
        binding.recyclerView2.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.recyclerView2.adapter= ReportListAdapter(mainViewModel.loadBudget())
        binding.recyclerView2.isNestedScrollingEnabled=false

    }
}