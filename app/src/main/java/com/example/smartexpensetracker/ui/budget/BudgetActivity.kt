package com.example.smartexpensetracker.ui.budget

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.smartexpensetracker.R
import com.example.smartexpensetracker.data.models.Transaction
import com.example.smartexpensetracker.databinding.ActivityBudgetBinding
import com.example.smartexpensetracker.ui.auth.AuthViewModel
import com.example.smartexpensetracker.ui.expenses.TransactionViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()
    private lateinit var prefs: SharedPreferences
    private var targetBudget: Double = 30000.0
    private lateinit var categoryAdapter: BudgetCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)
        targetBudget = prefs.getFloat("target_budget", 30000.0f).toDouble()

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        categoryAdapter = BudgetCategoryAdapter(emptyList())
        binding.rvBudgetCategories.adapter = categoryAdapter

        setupChart()

        binding.fabSetBudget.setOnClickListener {
            showSetBudgetTargetDialog()
        }
    }

    private fun setupChart() {
        binding.chartAnalysis.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            xAxis.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = true
            animateX(1500)
            setDrawGridBackground(false)
            setDrawBorders(false)
        }
    }

    private fun showSetBudgetTargetDialog() {
        val dialogBinding = com.example.smartexpensetracker.databinding.DialogSetBudgetBinding.inflate(layoutInflater)
        dialogBinding.etBudgetTarget.setText(targetBudget.toString())

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val newTarget = dialogBinding.etBudgetTarget.text.toString().toDoubleOrNull()
                if (newTarget != null) {
                    targetBudget = newTarget
                    prefs.edit().putFloat("target_budget", newTarget.toFloat()).apply()
                    transactionViewModel.transactions.value.let { updateBudgetUI(it) }
                    Toast.makeText(this, "Target budget updated!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.currentUser.collect { user ->
                        user?.let {
                            transactionViewModel.getTransactions(it.uid)
                        }
                    }
                }

                launch {
                    transactionViewModel.transactions.collect { transactions ->
                        updateBudgetUI(transactions)
                        updateChart(transactions)
                    }
                }
            }
        }
    }

    private fun updateBudgetUI(transactions: List<Transaction>) {
        val currentExpenses = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        val remaining = targetBudget - currentExpenses
        val progress = ((currentExpenses / targetBudget) * 100).toInt().coerceAtMost(100)

        binding.tvCurrentMonthBudget.text = "₹${String.format("%.2f", targetBudget)}"
        binding.tvBudgetSpent.text = "Spent: ₹${String.format("%.2f", currentExpenses)}"
        binding.tvBudgetRemaining.text = "Remaining: ₹${String.format("%.2f", remaining)}"
        binding.pbBudgetProgress.progress = progress
        binding.tvBudgetStatus.text = "You have used $progress% of your target budget"

        if (currentExpenses > targetBudget) {
            binding.pbBudgetProgress.progressTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.expenseColor))
            binding.tvBudgetStatus.setTextColor(getColor(R.color.expenseColor))
            binding.tvBudgetStatus.text = "Warning: You have exceeded your target budget!"
        } else {
            binding.pbBudgetProgress.progressTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.savingsColor))
            binding.tvBudgetStatus.setTextColor(getColor(R.color.white)) // Set to white for contrast on blue card
        }

        // Update categories
        val categoryData = transactions.filter { it.type == "expense" }
            .groupBy { it.categoryId }
            .map { (category, list) ->
                CategoryBudget(category, list.sumOf { it.amount }, targetBudget / 5) // Simplified: assume each category has 1/5 of total budget as target
            }
        categoryAdapter.updateData(categoryData)
    }

    private fun updateChart(transactions: List<Transaction>) {
        if (transactions.isEmpty()) return

        val incomeEntries = ArrayList<Entry>()
        val expenseEntries = ArrayList<Entry>()
        val savingsEntries = ArrayList<Entry>()

        val sortedTransactions = transactions.sortedBy { it.date }
        var currentIncome = 0.0
        var currentExpense = 0.0

        sortedTransactions.forEachIndexed { index, transaction ->
            if (transaction.type == "income") {
                currentIncome += transaction.amount
            } else {
                currentExpense += transaction.amount
            }
            incomeEntries.add(Entry(index.toFloat(), currentIncome.toFloat()))
            expenseEntries.add(Entry(index.toFloat(), currentExpense.toFloat()))
            savingsEntries.add(Entry(index.toFloat(), (currentIncome - currentExpense).toFloat()))
        }

        val incomeDataSet = LineDataSet(incomeEntries, "Income").apply {
            color = androidx.core.content.ContextCompat.getColor(this@BudgetActivity, R.color.expenseColor) // Red
            setCircleColor(androidx.core.content.ContextCompat.getColor(this@BudgetActivity, R.color.expenseColor))
            lineWidth = 5f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
            setDrawCircleHole(true)
            circleRadius = 4f
        }

        val expenseDataSet = LineDataSet(expenseEntries, "Expense").apply {
            color = androidx.core.content.ContextCompat.getColor(this@BudgetActivity, R.color.incomeColor) // Green
            setCircleColor(androidx.core.content.ContextCompat.getColor(this@BudgetActivity, R.color.incomeColor))
            lineWidth = 5f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
            setDrawCircleHole(true)
            circleRadius = 4f
        }

        val savingsDataSet = LineDataSet(savingsEntries, "Savings").apply {
            color = androidx.core.content.ContextCompat.getColor(this@BudgetActivity, R.color.savingsColor) // Blue
            setCircleColor(androidx.core.content.ContextCompat.getColor(this@BudgetActivity, R.color.savingsColor))
            lineWidth = 5f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
            setDrawCircleHole(true)
            circleRadius = 4f
        }

        binding.chartAnalysis.apply {
            data = LineData(incomeDataSet, expenseDataSet, savingsDataSet)
            
            // Add Rupee symbol to Y axis
            axisLeft.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "₹${value.toInt()}"
                }
            }
            
            invalidate()
        }
    }
}
