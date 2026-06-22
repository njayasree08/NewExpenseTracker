package com.example.smartexpensetracker.ui.expenses

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartexpensetracker.data.models.Transaction
import com.example.smartexpensetracker.databinding.ActivityAddTransactionBinding
import com.example.smartexpensetracker.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()
    private var selectedDate: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
        observeViewModel()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Professional Category List
        val categories = arrayOf(
            "Salary", "Freelance", "Bonus", "Stocks", "Gift",
            "Travel", "Food", "Movie", "Entertainment", "Recharge", 
            "Net", "Rent", "Loan", "EMI", "Shopping", "Others"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)

        updateDateDisplay()
    }

    private fun setupListeners() {
        binding.tilDate.setEndIconOnClickListener { showDatePicker() }
        binding.etDate.setOnClickListener { showDatePicker() }

        binding.btnSaveTransaction.setOnClickListener {
            saveTransaction()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.timeInMillis
                updateDateDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.etDate.setText(sdf.format(selectedDate))
    }

    private fun saveTransaction() {
        val amountStr = binding.etAmount.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        
        // Use Toggle Group for type
        val type = if (binding.toggleGroup.checkedButtonId == com.example.smartexpensetracker.R.id.btn_type_income) "income" else "expense"

        var isValid = true
        
        if (amountStr.isEmpty()) {
            binding.tilAmount.error = "Please enter an amount"
            isValid = false
        } else {
            binding.tilAmount.error = null
        }

        if (category.isEmpty()) {
            binding.tilCategory.error = "Please select a category"
            isValid = false
        } else {
            binding.tilCategory.error = null
        }

        if (!isValid) return

        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val userId = authViewModel.currentUser.value?.uid ?: ""

        if (userId.isEmpty()) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            userId = userId,
            amount = amount,
            type = type,
            categoryId = category,
            date = selectedDate,
            description = if (description.isEmpty()) category else description
        )

        transactionViewModel.addTransaction(transaction)
        finish()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            transactionViewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(this@AddTransactionActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
