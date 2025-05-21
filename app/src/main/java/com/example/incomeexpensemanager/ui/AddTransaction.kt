package com.example.incomeexpensemanager.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.data.AppDataBase
import com.example.incomeexpensemanager.data.Transaction
import kotlinx.coroutines.launch
import java.util.Calendar

class AddTransaction : AppCompatActivity() {

    private lateinit var db: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        db = AppDataBase.getInstance(this)

        val title = findViewById<EditText>(R.id.et_title)
        val description = findViewById<EditText>(R.id.et_description)
        val amount = findViewById<EditText>(R.id.et_amount)
        val typeSpinner = findViewById<Spinner>(R.id.spinner_type)
        val categorySpinner = findViewById<Spinner>(R.id.spinner_category)
        val date = findViewById<EditText>(R.id.et_date)
        val time = findViewById<EditText>(R.id.et_time)
        val save = findViewById<Button>(R.id.btn_save)

        val typeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.transaction_types,
            android.R.layout.simple_spinner_item
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter

        val categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.transaction_categories,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                date.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        time.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                time.setText(formattedTime)
            }, hour, minute, true)

            timePickerDialog.show()
        }

        save.setOnClickListener {
            val titleText = title.text.toString()
            val descriptionText = description.text.toString()
            val amountText = amount.text.toString()
            val dateText = date.text.toString()
            val timeText = time.text.toString()
            val typeText = typeSpinner.selectedItem?.toString()
            val categoryText = categorySpinner.selectedItem?.toString()

            if (titleText.isBlank() || amountText.isBlank() || typeText.isNullOrBlank() || categoryText.isNullOrBlank() || dateText.isBlank() || timeText.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amountValue = amountText.toDoubleOrNull()
            if (amountValue == null) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newTransaction = Transaction(
                title = titleText,
                description = descriptionText,
                amount = amountValue,
                type = typeText,
                category = categoryText,
                date = dateText,
                time = timeText
            )

            lifecycleScope.launch {
                db.transactionDao().insert(newTransaction)
                runOnUiThread {
                    Toast.makeText(this@AddTransaction, "Transaction added", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    }
}
