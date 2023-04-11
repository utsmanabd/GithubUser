package com.everybodv.githubuser.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.everybodv.githubuser.R
import com.everybodv.githubuser.databinding.ActivityMainBinding
import com.everybodv.githubuser.data.Result
import com.everybodv.githubuser.data.local.datastore.SettingsPreferences
import com.everybodv.githubuser.data.local.entity.UsersEntity
import com.everybodv.githubuser.model.*
import com.everybodv.githubuser.ui.adapter.UserFavoriteAdapter

class MainActivity : AppCompatActivity() {

    private var isDarkMode: Boolean = false

    private lateinit var binding: ActivityMainBinding

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsFactory(SettingsPreferences.getInstance(dataStore))
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsViewModel.getTheme().observe(this@MainActivity) { isDark: Boolean ->
            val theme = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(theme)
            isDarkMode = isDark
        }
        val viewModel: UserFavoriteViewModel by viewModels {
            ViewModelFactory.getInstance(application)
        }

        val userFavoriteAdapter = UserFavoriteAdapter { user ->
            if (user.isFavorite) {
                viewModel.deleteUsers(user)
                Toast.makeText(this@MainActivity, user.login + getString(R.string.success_remove), Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveUsers(user)
                Toast.makeText(this@MainActivity, user.login + getString(R.string.success_add), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.listUser.observe(this@MainActivity) { username ->
            viewModel.findUser(username).observe(this@MainActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Success -> {
                            setUserList(result.data, userFavoriteAdapter)
                        }
                        is Result.Loading -> showLoading(true)
                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(this@MainActivity, getString(R.string.error_conn), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        val layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)

        binding.rvListUser.apply {
            this.layoutManager = layoutManager
            setHasFixedSize(true)
            addItemDecoration(itemDecoration)
            adapter = userFavoriteAdapter
        }
    }

    private fun setUserList(listUser: List<UsersEntity>, adapter: UserFavoriteAdapter) {
        showLoading(false)
        if (listUser.isNotEmpty()) {
            adapter.submitList(null)
            adapter.submitList(listUser)
        } else {
            adapter.submitList(emptyList())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        val viewModel: UserFavoriteViewModel by viewModels {
            ViewModelFactory.getInstance(application)
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.setUsernameLiveData(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorite -> startActivity(
                Intent(this@MainActivity, UserFavoriteActivity::class.java)
            )
            R.id.menu_settings -> startActivity(
                Intent(this@MainActivity, SettingsActivity::class.java)
            )
        }
        return true
    }
}