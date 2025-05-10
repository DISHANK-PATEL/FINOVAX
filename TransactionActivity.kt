package com.example.finanaceapp.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.finanaceapp.Database.TransactionDatabase
import com.example.finanaceapp.Domain.Transaction
import com.example.finanaceapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var addBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        titleEditText = findViewById(R.id.title)
        timeEditText = findViewById(R.id.time)
        amountEditText = findViewById(R.id.money)
        addBtn = findViewById(R.id.add)

        val db = TransactionDatabase.getDatabase(this)
        val dao = db.transactionDao()

        addBtn.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val time = timeEditText.text.toString().trim()
            val amountText = amountEditText.text.toString().trim()

            if (title.isEmpty() || time.isEmpty() || amountText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(this, "Enter a valid number for amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transaction = Transaction(
                title = title,
                amount = amount,
                timestamp = time
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    dao.insertTransaction(transaction)
                }
                Toast.makeText(this@TransactionActivity, "Transaction added", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}