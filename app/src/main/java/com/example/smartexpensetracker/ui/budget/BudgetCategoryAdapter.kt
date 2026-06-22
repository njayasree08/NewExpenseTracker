package com.example.smartexpensetracker.ui.budget

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensetracker.R
import com.example.smartexpensetracker.databinding.ItemBudgetCategoryBinding

data class CategoryBudget(
    val name: String,
    val spent: Double,
    val budget: Double
)

class BudgetCategoryAdapter(private var items: List<CategoryBudget>) : 
    RecyclerView.Adapter<BudgetCategoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBudgetCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBudgetCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvCategoryName.text = item.name
        holder.binding.tvCategoryBudgetAmount.text = "₹${String.format("%.2f", item.budget)}"
        holder.binding.tvCategorySpentAmount.text = "Spent: ₹${String.format("%.2f", item.spent)}"
        
        val savings = item.budget - item.spent
        holder.binding.tvCategorySavings.text = if (savings >= 0) "Saved: ₹${String.format("%.2f", savings)}" else "Over: ₹${String.format("%.2f", -savings)}"
        holder.binding.tvCategorySavings.setTextColor(ContextCompat.getColor(holder.binding.root.context, if (savings >= 0) R.color.savingsColor else R.color.expenseColor))

        val progress = if (item.budget > 0) ((item.spent / item.budget) * 100).toInt().coerceAtMost(100) else 0
        holder.binding.pbCategoryBudgetProgress.progress = progress
        
        if (item.spent > item.budget) {
            holder.binding.pbCategoryBudgetProgress.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.binding.root.context, R.color.expenseColor))
        } else {
            holder.binding.pbCategoryBudgetProgress.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.binding.root.context, R.color.primaryColor))
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<CategoryBudget>) {
        items = newItems
        notifyDataSetChanged()
    }
}
