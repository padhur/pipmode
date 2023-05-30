package com.example.android.pictureinpicture.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.pictureinpicture.interfaces.TimeProvider

class MainViewModelFactory(private val timeProvider: TimeProvider) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(timeProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
