package com.example.smartexpensetracker.ui.history

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartexpensetracker.databinding.ActivityHistoryBinding
import com.example.smartexpensetracker.ui.auth.AuthViewModel
import com.example.smartexpensetracker.ui.dashboard.TransactionAdapter
import com.example.smartexpensetracker.ui.expenses.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = TransactionAdapter()
        binding.rvTransactionHistory.layoutManager = LinearLayoutManager(this)
        binding.rvTransactionHistory.adapter = adapter

        binding.etSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterTransactions(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterTransactions(newText ?: "")
                return true
            }
        })

        setupSwipeToDelete()
    }

    private fun setupSwipeToDelete() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val transaction = adapter.currentList[position]
                
                androidx.appcompat.app.AlertDialog.Builder(this@HistoryActivity)
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Delete") { _, _ ->
                        transactionViewModel.deleteTransaction(transaction.id)
                        Toast.makeText(this@HistoryActivity, "Transaction deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
                    .setOnCancelListener {
                        adapter.notifyItemChanged(position)
                    }
                    .show()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvTransactionHistory)
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
                        adapter.submitList(transactions)
                        binding.tvEmptyHistory.visibility = if (transactions.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                    }
                }

                launch {
                    transactionViewModel.error.collect { error ->
                        error?.let {
                            Toast.makeText(this@HistoryActivity, it, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun filterTransactions(query: String) {
        val fullList = transactionViewModel.transactions.value
        val filteredList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter {
                it.description.contains(query, ignoreCase = true) ||
                it.categoryId.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filteredList)
        binding.tvEmptyHistory.visibility = if (filteredList.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }
}
