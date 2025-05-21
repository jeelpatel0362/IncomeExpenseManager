package com.example.incomeexpensemanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
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
    private var transactionList: List<Transaction> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        db = AppDataBase.getInstance(this)

        val rvTransactions = findViewById<RecyclerView>(R.id.rv_transactions)
        val search = findViewById<EditText>(R.id.et_search)
        val spinnerCategory = findViewById<Spinner>(R.id.spinner_month)
        val btnAdd = findViewById<Button>(R.id.btn_add)

        rvTransactions.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter(this@HomePage, emptyList())
        rvTransactions.adapter = adapter

        db.transactionDao().getAllTransactions().observe(this) { transactions ->
            transactionList = transactions
            adapter.updateList(transactions)
            updateTotals(transactions)
        }

        val categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.transaction_categories,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                applyFilters(
                    selectedCategory = parent.getItemAtPosition(pos).toString(),
                    searchQuery = search.text.toString()
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        search.addTextChangedListener {
            applyFilters(
                selectedCategory = spinnerCategory.selectedItem.toString(),
                searchQuery = it.toString()
            )
        }

        btnAdd.setOnClickListener {
            startActivityForResult(Intent(this, AddTransaction::class.java), 1)
        }
    }

    private fun applyFilters(selectedCategory: String, searchQuery: String) {
        val filtered = transactionList.filter { transaction ->
            val matchCategory = selectedCategory == "All" || transaction.category.equals(selectedCategory, true)
            val matchSearch = searchQuery.isBlank() || transaction.category.contains(searchQuery, true)
            matchCategory && matchSearch
        }

        adapter.updateList(filtered)
        updateTotals(filtered)
    }

    private fun updateTotals(transactions: List<Transaction>) {
        val totalIncome = transactions.filter { it.type == "Income" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }

        findViewById<TextView>(R.id.tv_total_income).text = "Income: ₹$totalIncome"
        findViewById<TextView>(R.id.tv_total_expense).text = "Expense: ₹$totalExpense"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            db.transactionDao().getAllTransactions().observe(this) { transactions ->
                transactionList = transactions
                adapter.updateList(transactionList)
                updateTotals(transactions)
            }
        }
    }
}
