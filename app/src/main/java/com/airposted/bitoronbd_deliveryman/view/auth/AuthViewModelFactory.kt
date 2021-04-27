package com.airposted.bitoronbd_deliveryman.view.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.airposted.bitoronbd_deliveryman.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T
    }
}