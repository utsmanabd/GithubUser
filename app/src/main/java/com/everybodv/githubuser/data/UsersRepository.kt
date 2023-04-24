package com.everybodv.githubuser.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.everybodv.githubuser.data.local.entity.UsersEntity
import com.everybodv.githubuser.data.local.room.UsersDao
import com.everybodv.githubuser.data.remote.response.GithubDetailResponse
import com.everybodv.githubuser.data.remote.response.GithubUserResponse
import com.everybodv.githubuser.data.remote.response.ItemsItem
import com.everybodv.githubuser.data.remote.retrofit.ApiService
import com.everybodv.githubuser.model.DetailUserViewModel
import com.everybodv.githubuser.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersRepository private constructor(
    private val apiService: ApiService,
    private val usersDao: UsersDao,
    private val appExecutors: AppExecutors
){

    private val users = MediatorLiveData<Result<List<UsersEntity>>>()
    private val detail = MediatorLiveData<Result<GithubDetailResponse>>()
    private val following = MediatorLiveData<Result<List<UsersEntity>>>()
    private val followers = MediatorLiveData<Result<List<UsersEntity>>>()

    private val _isFavorite = MediatorLiveData<Boolean>()
    val isFavorite : MutableLiveData<Boolean> =_isFavorite

    fun isUserFavorite(username: String) : LiveData<Boolean> {
        appExecutors.diskIO.execute {
            val isFav = usersDao.isUserFavorite(username)
            _isFavorite.postValue(isFav)
        }
        return _isFavorite
    }

    fun findUser(query: String) : LiveData<Result<List<UsersEntity>>> {
        users.value = Result.Loading
        val client = apiService.getUser(query)
        client.enqueue(object : Callback<GithubUserResponse> {
            override fun onResponse(
                call: Call<GithubUserResponse>,
                response: Response<GithubUserResponse>
            ) {
                if (response.isSuccessful) {
                    val listUser = ArrayList<UsersEntity>()
                    val users = response.body()?.items
                    appExecutors.diskIO.execute {
                        users?.forEach { user ->
                            val isFavorite = usersDao.isUserFavorite(user.login)
                            val userEntity = UsersEntity(
                                user.login,
                                user.avatarUrl,
                                isFavorite
                            )
                            listUser.add(userEntity)
                        }
                        this@UsersRepository.users.postValue(Result.Success(listUser))
                    }

                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GithubUserResponse>, t: Throwable) {
                users.value = Result.Error(t.message.toString())
            }

        })
        return users
    }

    suspend fun setFavUser(users: UsersEntity, isFavorite: Boolean) {
        users.isFavorite = isFavorite
        if (isFavorite) {
            usersDao.insert(users)
        } else {
            usersDao.delete(users)
        }
    }

    fun getFavoriteUsers(): LiveData<List<UsersEntity>> {
        return usersDao.getFavoriteUser()
    }

    fun getDetailUser(query: String = ""): LiveData<Result<GithubDetailResponse>> {
        detail.value = Result.Loading

        val client = apiService.getDetail(query)
        client.enqueue(object : Callback<GithubDetailResponse> {
            override fun onResponse(
                call: Call<GithubDetailResponse>,
                response: Response<GithubDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val detailResponse = response.body() as GithubDetailResponse
                    detail.value = Result.Success(detailResponse)

                    getFollowing(query)
                    getFollowers(query)
                } else {
                    Log.e(DetailUserViewModel.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GithubDetailResponse>, t: Throwable) {
                detail.value = Result.Error(t.message.toString())
            }

        })
        return detail
    }

    fun getFollowing(query: String = "") {
        following.value = Result.Loading

        val client = apiService.getFollowing(query)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                if (response.isSuccessful) {
                    val listFollowing = ArrayList<UsersEntity>()
                    val follow = response.body()
                    appExecutors.diskIO.execute {
                        follow?.forEach { user ->
                            val isFavorite = usersDao.isUserFavorite(user.login)
                            val usersEntity = UsersEntity(user.login, user.avatarUrl, isFavorite)
                            listFollowing.add(usersEntity)
                        }
                        following.postValue(Result.Success(listFollowing))
                    }
                } else {
                    Log.e(DetailUserViewModel.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                following.value = Result.Error(t.message.toString())
            }

        })
    }

    fun getFollowers(query: String = "") {
        followers.value = Result.Loading

        val client = apiService.getFollowers(query)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                if (response.isSuccessful) {
                    val listFollowers = ArrayList<UsersEntity>()
                    val follow = response.body()
                    appExecutors.diskIO.execute {
                        follow?.forEach { user ->
                            val isFavorite = usersDao.isUserFavorite(user.login)
                            val usersEntity = UsersEntity(user.login, user.avatarUrl, isFavorite)
                            listFollowers.add(usersEntity)
                        }
                        followers.postValue(Result.Success(listFollowers))
                    }
                } else {
                    Log.e(DetailUserViewModel.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                followers.value = Result.Error(t.message.toString())
            }

        })
    }

    companion object {
        private const val TAG = "UsersRepository"
        @Volatile
        private var instance: UsersRepository? = null
        fun getInstance(
            apiService: ApiService,
            usersDao: UsersDao,
            appExecutors: AppExecutors
        ) : UsersRepository =
            instance ?: synchronized(this) {
                instance ?: UsersRepository(apiService, usersDao, appExecutors)
            }.also { instance = it }
    }
}