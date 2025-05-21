package com.example.incomeexpensemanager.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
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
        val type = findViewById<Spinner>(R.id.spinner_type)
        val category = findViewById<Spinner>(R.id.spinner_category)
        val date = findViewById<EditText>(R.id.et_date)
        val time = findViewById<EditText>(R.id.et_time)
        val save = findViewById<Button>(R.id.btn_save)

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
            val newTransaction = Transaction(
                title = title.text.toString(),
                description = description.text.toString(),
                amount = amount.text.toString().toDouble(),
                type = type.selectedItem.toString(),
                category = category.selectedItem.toString(),
                date = date.text.toString(),
                time = time.text.toString()
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
