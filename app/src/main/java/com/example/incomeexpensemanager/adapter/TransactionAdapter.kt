package com.example.incomeexpensemanager.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.data.Transaction

import com.example.incomeexpensemanager.ui.UpdateDeleteScreen

class TransactionAdapter(
    private val context: Context,
    private var transactions: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {


    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        val tvDateTime: TextView = itemView.findViewById(R.id.tv_date_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvTitle.text = transaction.title
        holder.tvDescription.text = transaction.description
        holder.tvAmount.text = "₹${transaction.amount}"
        holder.tvAmount.setTextColor(
            if (transaction.type == "Income")
                context.getColor(android.R.color.holo_green_dark)
            else
                context.getColor(android.R.color.holo_red_dark)
        )
        holder.tvDateTime.text = "${transaction.date} ${transaction.time}"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, UpdateDeleteScreen::class.java)
            intent.putExtra("transaction", transaction)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = transactions.size

    fun updateList(newList: List<Transaction>) {
        transactions = newList
        notifyDataSetChanged()
    }
}
