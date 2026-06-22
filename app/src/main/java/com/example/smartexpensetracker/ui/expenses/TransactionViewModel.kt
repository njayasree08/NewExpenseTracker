package com.example.smartexpensetracker.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensetracker.data.models.Transaction
import com.example.smartexpensetracker.data.remote.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getTransactions(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            transactionRepository.getTransactions(userId).collect {
                _transactions.value = it
                _isLoading.value = false
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = transactionRepository.addTransaction(transaction)
            result.onFailure { exception ->
                _error.value = exception.message
            }
            _isLoading.value = false
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = transactionRepository.updateTransaction(transaction)
            result.onFailure { exception ->
                _error.value = exception.message
            }
            _isLoading.value = false
        }
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = transactionRepository.deleteTransaction(transactionId)
            result.onFailure { exception ->
                _error.value = exception.message
            }
            _isLoading.value = false
        }
    }
}
