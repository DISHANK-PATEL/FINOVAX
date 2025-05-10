package com.example.finanaceapp.Activity

import android.os.Bundle
import android.provider.Telephony
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finanaceapp.Adapter.MessageAdapter
import com.example.finanaceapp.Database.TransactionDatabase
import com.example.finanaceapp.Database.TransactionDao
import com.example.finanaceapp.Domain.MessageTransaction
import com.example.finanaceapp.Domain.Transaction
import com.example.finanaceapp.databinding.MessageActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: MessageActivityBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var transactionDao: TransactionDao
    private val messageList = mutableListOf<MessageTransaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MessageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val database = TransactionDatabase.getDatabase(this)
        transactionDao = database.transactionDao()


        fetchTransactionMessages()
    }

    private fun fetchTransactionMessages() {
        messageList.clear()

        val cursor = contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            null,
            null,
            null,
            "date DESC LIMIT 50"
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
                val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))

                if (body.contains("Rs.", ignoreCase = true)) {
                    val amount = extractAmountSimple(body)
                    val title = address
                    val timestamp = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date(date))

                    messageList.add(MessageTransaction(title, amount, timestamp))
                }
            } while (cursor.moveToNext())
            cursor.close()
        }

        if (messageList.isEmpty()) {
            Toast.makeText(this, "No messages with Rs. found.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "${messageList.size} messages found", Toast.LENGTH_SHORT).show()
        }


        messageAdapter = MessageAdapter(
            transactions = messageList,
            onAddClicked = { msg ->
                CoroutineScope(Dispatchers.IO).launch {
                    val transaction = Transaction(
                        title = msg.title,
                        amount = msg.amount,
                        timestamp = msg.timestamp
                    )
                    transactionDao.insertTransaction(transaction)
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@MessageActivity, "Added to transaction history", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onEditClicked = { message, position ->
                showEditDialog(message, position)
            }
        )

        binding.recyclerView.adapter = messageAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun showEditDialog(transaction: MessageTransaction, position: Int) {
        val editText = EditText(this)
        editText.setText(transaction.title)

        AlertDialog.Builder(this)
            .setTitle("Edit Title")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newTitle = editText.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    val updated = transaction.copy(title = newTitle)
                    messageList[position] = updated
                    messageAdapter.notifyItemChanged(position)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun extractAmountSimple(body: String): Double {
        val regex = Regex("Rs\\.\\s?([0-9]+\\.?[0-9]*)")
        val matchResult = regex.find(body)
        return matchResult?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
    }
}