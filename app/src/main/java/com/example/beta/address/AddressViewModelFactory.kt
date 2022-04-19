package com.example.beta.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddressViewModelFactory(private val username: String):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddressViewModel(username) as T
    }
}
