package com.example.incomeexpensemanager.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.data.AppDataBase
import com.example.incomeexpensemanager.data.Transaction
import kotlinx.coroutines.launch

class UpdateDeleteScreen : AppCompatActivity() {

    private lateinit var db: AppDataBase
    private var transaction: Transaction? = null

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etAmount: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var spinnerCategory: Spinner
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_delete_screen)

        db = Room.databaseBuilder(applicationContext, AppDataBase::class.java, "TransactionDB").build()

        etTitle = findViewById(R.id.et_title)
        etDescription = findViewById(R.id.et_description)
        etAmount = findViewById(R.id.et_amount)
        spinnerType = findViewById(R.id.spinner_type)
        spinnerCategory = findViewById(R.id.spinner_category)
        etDate = findViewById(R.id.et_date)
        etTime = findViewById(R.id.et_time)
        btnUpdate = findViewById(R.id.btn_update)
        btnDelete = findViewById(R.id.btn_delete)

        transaction = intent.getSerializableExtra("transaction") as? Transaction

        transaction?.let {
            etTitle.setText(it.title)
            etDescription.setText(it.description)
            etAmount.setText(it.amount.toString())
            etDate.setText(it.date)
            etTime.setText(it.time)
        }

        btnUpdate.setOnClickListener {
            updateTransaction()
        }

        btnDelete.setOnClickListener {
            deleteTransaction()
        }
    }

    private fun updateTransaction() {
        transaction?.let {
            val updated = it.copy(
                title = etTitle.text.toString(),
                description = etDescription.text.toString(),
                amount = etAmount.text.toString().toDouble(),
                type = spinnerType.selectedItem.toString(),
                category = spinnerCategory.selectedItem.toString(),
                date = etDate.text.toString(),
                time = etTime.text.toString()
            )

            lifecycleScope.launch {
                db.transactionDao().update(updated)
                runOnUiThread {
                    Toast.makeText(this@UpdateDeleteScreen, "Transaction updated", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun deleteTransaction() {
        transaction?.let {
            lifecycleScope.launch {
                db.transactionDao().delete(it)
                runOnUiThread {
                    Toast.makeText(this@UpdateDeleteScreen, "Transaction deleted", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }
}
