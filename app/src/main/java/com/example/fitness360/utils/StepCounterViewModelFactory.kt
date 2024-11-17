package com.example.fitness360.utils
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StepCounterViewModelFactory(
    private val application: Application,
    private val uid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepCounterViewModel::class.java)) {
            return StepCounterViewModel(application, uid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}