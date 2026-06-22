package com.example.smartexpensetracker.ui.dashboard

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensetracker.R
import com.example.smartexpensetracker.data.models.Transaction
import com.example.smartexpensetracker.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.tvTransactionDescription.text = transaction.description
            binding.tvTransactionCategory.text = transaction.categoryId // Ideally resolve category name
            binding.tvTransactionDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(transaction.date))
            
            val amountText = if (transaction.type == "income") "+ ₹${transaction.amount}" else "- ₹${transaction.amount}"
            binding.tvTransactionAmount.text = amountText
            
            if (transaction.type == "income") {
                binding.tvTransactionAmount.setTextColor(ContextCompat.getColor(binding.root.context, R.color.incomeColor))
                binding.categoryIconBg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.incomeLight))
                binding.ivTransactionType.setImageResource(android.R.drawable.ic_input_add)
                binding.ivTransactionType.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.incomeColor))
            } else {
                binding.tvTransactionAmount.setTextColor(ContextCompat.getColor(binding.root.context, R.color.expenseColor))
                binding.categoryIconBg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.expenseLight))
                binding.ivTransactionType.setImageResource(android.R.drawable.ic_delete)
                binding.ivTransactionType.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.expenseColor))
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean = oldItem == newItem
    }
}
