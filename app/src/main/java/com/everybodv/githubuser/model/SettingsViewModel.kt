package com.everybodv.githubuser.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.everybodv.githubuser.data.local.datastore.SettingsPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(private val preferences: SettingsPreferences) : ViewModel() {

    fun saveTheme(isDarkMode: Boolean) {
        viewModelScope.launch {
            preferences.saveThemeSetting(isDarkMode)
        }
    }

    fun getTheme(): LiveData<Boolean> = preferences.getThemeSetting().asLiveData()
}