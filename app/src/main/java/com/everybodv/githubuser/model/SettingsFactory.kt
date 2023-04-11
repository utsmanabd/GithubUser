package com.everybodv.githubuser.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.everybodv.githubuser.data.local.datastore.SettingsPreferences

class SettingsFactory (private val preferences: SettingsPreferences) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}