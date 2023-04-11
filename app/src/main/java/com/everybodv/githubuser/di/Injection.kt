package com.everybodv.githubuser.di

import android.content.Context
import com.everybodv.githubuser.data.UsersRepository
import com.everybodv.githubuser.data.local.room.UsersDatabase
import com.everybodv.githubuser.data.remote.retrofit.ApiConfig
import com.everybodv.githubuser.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): UsersRepository {
        val apiService = ApiConfig.getApiService()
        val database = UsersDatabase.getInstance(context)
        val dao = database.usersDao()
        val appExecutors = AppExecutors()
        return UsersRepository.getInstance(apiService, dao, appExecutors)
    }
}