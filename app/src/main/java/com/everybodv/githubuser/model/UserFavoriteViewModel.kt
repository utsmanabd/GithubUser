package com.everybodv.githubuser.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everybodv.githubuser.data.UsersRepository
import com.everybodv.githubuser.data.local.entity.UsersEntity
import kotlinx.coroutines.launch

class UserFavoriteViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    private val _listUser = MutableLiveData<String>()
    val listUser: LiveData<String> = _listUser

    val isFavorite : MutableLiveData<Boolean> = usersRepository.isFavorite

    init {
        setUsernameLiveData()
    }

    fun setUsernameLiveData(username: String = USERNAME) {
        _listUser.value = username
    }

    companion object {
        private const val USERNAME = "taka"
    }

    fun findUser(query: String) = usersRepository.findUser(query)

    fun getDetail(query: String) = usersRepository.getDetailUser(query)

    fun getFavoriteUsers() = usersRepository.getFavoriteUsers()

    fun saveUsers(users: UsersEntity) {
        viewModelScope.launch { usersRepository.setFavUser(users, true) }
    }

    fun deleteUsers(users: UsersEntity) {
        viewModelScope.launch { usersRepository.setFavUser(users, false) }
    }

    fun isUserFavorite(username: String) =
        usersRepository.isUserFavorite(username)
}