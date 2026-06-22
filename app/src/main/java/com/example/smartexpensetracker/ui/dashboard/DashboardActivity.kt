package com.example.smartexpensetracker.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartexpensetracker.R
import com.example.smartexpensetracker.data.models.Transaction
import com.example.smartexpensetracker.databinding.ActivityDashboardBinding
import com.example.smartexpensetracker.ui.auth.AuthViewModel
import com.example.smartexpensetracker.ui.expenses.AddTransactionActivity
import com.example.smartexpensetracker.ui.expenses.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_theme -> {
                toggleTheme()
                true
            }
            R.id.action_add_samples -> {
                addSampleData()
                true
            }
            R.id.action_logout -> {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout") { _, _ ->
                        authViewModel.logout()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleTheme() {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun addSampleData() {
        val userId = authViewModel.currentUser.value?.uid ?: return
        
        val samples = listOf(
            // 5 Income
            Transaction(UUID.randomUUID().toString(), userId, 50000.0, "income", "Salary", System.currentTimeMillis() - 86400000 * 5, "Monthly Salary"),
            Transaction(UUID.randomUUID().toString(), userId, 2000.0, "income", "Freelance", System.currentTimeMillis() - 86400000 * 4, "Project A"),
            Transaction(UUID.randomUUID().toString(), userId, 1500.0, "income", "Bonus", System.currentTimeMillis() - 86400000 * 3, "Performance Bonus"),
            Transaction(UUID.randomUUID().toString(), userId, 1000.0, "income", "Stocks", System.currentTimeMillis() - 86400000 * 2, "Dividends"),
            Transaction(UUID.randomUUID().toString(), userId, 500.0, "income", "Gift", System.currentTimeMillis() - 86400000 * 1, "Birthday Gift"),
            
            // 4 Expense
            Transaction(UUID.randomUUID().toString(), userId, 12000.0, "expense", "Rent", System.currentTimeMillis() - 86400000 * 4, "Monthly Rent"),
            Transaction(UUID.randomUUID().toString(), userId, 3000.0, "expense", "Food", System.currentTimeMillis() - 86400000 * 3, "Groceries"),
            Transaction(UUID.randomUUID().toString(), userId, 1500.0, "expense", "Travel", System.currentTimeMillis() - 86400000 * 2, "Fuel"),
            Transaction(UUID.randomUUID().toString(), userId, 800.0, "expense", "Entertainment", System.currentTimeMillis() - 86400000 * 1, "Movie Night")
        )

        samples.forEach { transactionViewModel.addTransaction(it) }
        Toast.makeText(this, "Sample data added!", Toast.LENGTH_SHORT).show()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // We use custom title centering in XML if needed, or just let toolbar handle it

        adapter = TransactionAdapter()
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvRecentTransactions.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        binding.cardAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        binding.tvViewAll.setOnClickListener {
            startActivity(Intent(this, com.example.smartexpensetracker.ui.history.HistoryActivity::class.java))
        }

        binding.cardBudgetAnalysis.setOnClickListener {
            startActivity(Intent(this, com.example.smartexpensetracker.ui.budget.BudgetActivity::class.java))
        }

        binding.cardViewHistory.setOnClickListener {
            startActivity(Intent(this, com.example.smartexpensetracker.ui.history.HistoryActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.currentUser.collect { user ->
                        if (user != null) {
                            binding.tvWelcomeMessage.text = "Hello, ${user.email}!"
                            transactionViewModel.getTransactions(user.uid)
                        } else {
                            startActivity(Intent(this@DashboardActivity, com.example.smartexpensetracker.ui.auth.LoginActivity::class.java))
                            finish()
                        }
                    }
                }

                launch {
                    transactionViewModel.transactions.collect { transactions ->
                        // Display only the 5 most recent transactions
                        val recent = transactions.sortedByDescending { it.date }.take(5)
                        adapter.submitList(recent)
                        
                        binding.tvEmptyTransactions.visibility = if (recent.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE

                        updateSummary(transactions)
                    }
                }

                launch {
                    transactionViewModel.error.collect { error ->
                        error?.let {
                            Toast.makeText(this@DashboardActivity, it, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun updateSummary(transactions: List<Transaction>) {
        val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val expenses = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        val balance = income - expenses

        binding.tvTotalIncome.text = "₹${String.format("%.2f", income)}"
        binding.tvTotalExpenses.text = "₹${String.format("%.2f", expenses)}"
        binding.tvCurrentBalance.text = "₹${String.format("%.2f", balance)}"
    }
}
