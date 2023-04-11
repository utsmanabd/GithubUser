package com.everybodv.githubuser.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.everybodv.githubuser.R
import com.everybodv.githubuser.data.local.datastore.SettingsPreferences
import com.everybodv.githubuser.data.local.entity.UsersEntity
import com.everybodv.githubuser.ui.adapter.SectionPagerAdapter
import com.everybodv.githubuser.data.remote.response.GithubDetailResponse
import com.everybodv.githubuser.databinding.ActivityDetailUserBinding
import com.everybodv.githubuser.model.*
import com.everybodv.githubuser.ui.dataStore
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.everybodv.githubuser.data.Result
import com.everybodv.githubuser.ui.SettingsActivity
import com.everybodv.githubuser.ui.UserFavoriteActivity

class DetailUserActivity : AppCompatActivity() {

    private var _binding: ActivityDetailUserBinding? = null
    private val binding get() = _binding!!

    private var isDarkMode: Boolean = false
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsFactory(SettingsPreferences.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_user)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(application)
        val viewModel: UserFavoriteViewModel by viewModels { factory }

        settingsViewModel.getTheme().observe(this@DetailUserActivity) { isDark: Boolean ->
            val theme = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(theme)
            isDarkMode = isDark
        }

        val receiveUsersEntity = intent.getParcelableExtra<UsersEntity>(EXTRA_USER) as UsersEntity

        val sectionPagerAdapter = SectionPagerAdapter(this)
        sectionPagerAdapter.userName = receiveUsersEntity.login
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.adapter = sectionPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_POSITION[position])
        }.attach()

        val fbFavorite = binding.fbFavorite

        val favorite = receiveUsersEntity.isFavorite
        if (!favorite) {
            fbFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
        } else fbFavorite.setImageResource(R.drawable.baseline_favorite_24)

        fbFavorite.setOnClickListener {
            val (login, avatarUrl, isFavorite) = receiveUsersEntity
            if (!isFavorite) {
                fbFavorite.setImageResource(R.drawable.baseline_favorite_24)
                viewModel.saveUsers(UsersEntity(login, avatarUrl, isFavorite))
                fbFavorite.isEnabled = false
                Toast.makeText(this@DetailUserActivity, login + getString(R.string.success_add), Toast.LENGTH_SHORT).show()
            } else {
                fbFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
                viewModel.deleteUsers(UsersEntity(login, avatarUrl, isFavorite))
                fbFavorite.isEnabled = false
                Toast.makeText(this@DetailUserActivity, login + getString(R.string.success_remove), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getDetail(receiveUsersEntity.login).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Success -> {
                        showLoading(false)
                        setDetail(result.data)
                    }
                    is Result.Loading -> showLoading(true)
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this@DetailUserActivity, getString(R.string.error_conn), Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }

    }
    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        menu?.findItem(R.id.search)?.isVisible = false

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorite -> startActivity(
                Intent(this@DetailUserActivity, UserFavoriteActivity::class.java))
            R.id.menu_settings -> startActivity(
                Intent(this@DetailUserActivity, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarDetail.visibility = View.VISIBLE
        } else {
            binding.progressBarDetail.visibility = View.GONE
        }
    }

    private fun setDetail(name: GithubDetailResponse?) {
        Glide.with(this@DetailUserActivity)
            .load(name?.avatarUrl)
            .into(binding.detailImage)
        binding.tvDetailName.text = name?.name
        binding.tvUsername.text = name?.login
        binding.tvNumFollowers.text = name?.followers.toString()
        binding.tvNumFollowing.text = name?.following.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_USER = "key_user"
        @StringRes
        private val TAB_POSITION = intArrayOf(
            R.string.followers,
            R.string.following
        )
    }
}