package com.example.fitness360.utils
import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StepCounterViewModelFactory(
    private val application: Application,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepCounterViewModel::class.java)) {
            return StepCounterViewModel(application, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}