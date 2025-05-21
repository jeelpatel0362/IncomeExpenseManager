package com.example.incomeexpensemanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.adapter.TransactionAdapter
import com.example.incomeexpensemanager.data.AppDataBase
import com.example.incomeexpensemanager.data.Transaction

class HomePage : AppCompatActivity() {

    private lateinit var db: AppDataBase
    private lateinit var adapter: TransactionAdapter
    private lateinit var transactionList: List<Transaction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        db = AppDataBase.getInstance(this)

        val rvTransactions = findViewById<RecyclerView>(R.id.rv_transactions)
        val search = findViewById<EditText>(R.id.et_search)
        val spinnerMonth = findViewById<Spinner>(R.id.spinner_month)
        val btnAdd = findViewById<Button>(R.id.btn_add)

        rvTransactions.layoutManager = LinearLayoutManager(this)

        adapter = TransactionAdapter(this@HomePage, emptyList<Transaction>())
        rvTransactions.adapter = adapter

        db.transactionDao().getAllTransactions().observe(this) { transactions ->
            transactionList = transactions
            adapter.updateList(transactions)
            updateTotals(transactions)
        }

        search.addTextChangedListener {
            val keyword = it.toString()
            val filtered = transactionList.filter { t -> t.category.contains(keyword, true) }
            adapter.updateList(filtered)
        }

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val month = parent.getItemAtPosition(pos).toString()
                db.transactionDao().getTransactionsByMonth(month).observe(this@HomePage) {
                    adapter.updateList(it)
                    updateTotals(it)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddTransaction::class.java))
        }
    }

    private fun updateTotals(transactions: List<Transaction>) {
        val totalIncome = transactions.filter { it.type == "Income" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }

        findViewById<TextView>(R.id.tv_total_income).text = "Income: ₹$totalIncome"
        findViewById<TextView>(R.id.tv_total_expense).text = "Expense: ₹$totalExpense"
    }
}
