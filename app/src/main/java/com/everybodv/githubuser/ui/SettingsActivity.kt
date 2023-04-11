package com.everybodv.githubuser.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.everybodv.githubuser.R
import com.everybodv.githubuser.data.local.datastore.SettingsPreferences
import com.everybodv.githubuser.databinding.ActivityMainBinding
import com.everybodv.githubuser.model.SettingsFactory
import com.everybodv.githubuser.model.SettingsViewModel
import com.everybodv.githubuser.model.ViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings)

        val switchToDark= findViewById<SwitchMaterial>(R.id.switch_to_dark)

        val pref = SettingsPreferences.getInstance(dataStore)
        val settingsViewModel = ViewModelProvider(this, SettingsFactory(pref)).get(
            SettingsViewModel::class.java
        )

        settingsViewModel.getTheme().observe(this) { isDark: Boolean ->
            if (isDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchToDark.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchToDark.isChecked = false
            }
        }

        switchToDark.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
        settingsViewModel.saveTheme(isChecked)}
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}