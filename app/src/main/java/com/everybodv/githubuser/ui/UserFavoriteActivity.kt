package com.everybodv.githubuser.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.everybodv.githubuser.R
import com.everybodv.githubuser.data.local.datastore.SettingsPreferences
import com.everybodv.githubuser.databinding.ActivityUserFavoriteBinding
import com.everybodv.githubuser.model.SettingsFactory
import com.everybodv.githubuser.model.SettingsViewModel
import com.everybodv.githubuser.model.UserFavoriteViewModel
import com.everybodv.githubuser.model.ViewModelFactory
import com.everybodv.githubuser.ui.adapter.UserFavoriteAdapter

class UserFavoriteActivity : AppCompatActivity() {

    private var _activityUserFavorite: ActivityUserFavoriteBinding? = null
    private val binding get() = _activityUserFavorite

    private lateinit var userFavoriteAdapter: UserFavoriteAdapter

    private var isDarkMode: Boolean = false
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsFactory(SettingsPreferences.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityUserFavorite = ActivityUserFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_fav)

        settingsViewModel.getTheme().observe(this@UserFavoriteActivity) { isDark: Boolean ->
            val theme = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(theme)
            isDarkMode = isDark
        }

        val factory : ViewModelFactory = ViewModelFactory.getInstance(application)
        val viewModel: UserFavoriteViewModel by viewModels {
            factory
        }

        userFavoriteAdapter = UserFavoriteAdapter { user ->
            if (user.isFavorite) {
                viewModel.deleteUsers(user)
                Toast.makeText(this@UserFavoriteActivity, user.login + getString(R.string.success_remove), Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveUsers(user)
                Toast.makeText(this@UserFavoriteActivity, user.login + getString(R.string.success_add), Toast.LENGTH_SHORT).show()
            }
        }

        showLoading(true)

        viewModel.getFavoriteUsers().observe(this) { favorite ->
            if (favorite != null) {
                showLoading(false)
                userFavoriteAdapter.submitList(favorite)
                binding?.userFavorite?.apply {
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(true)
                    adapter = userFavoriteAdapter
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.favProgressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityUserFavorite = null
    }
}