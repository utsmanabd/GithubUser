package com.everybodv.githubuser.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.everybodv.githubuser.data.local.entity.UsersEntity

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(users: UsersEntity)

    @Delete
    suspend fun delete(users: UsersEntity)

    @Query("SELECT * FROM users WHERE is_favorite = 1")
    fun getFavoriteUser(): LiveData<List<UsersEntity>>

    @Query("SELECT EXISTS (SELECT * FROM users WHERE username = :username AND is_favorite = 1)")
    fun isUserFavorite(username: String): Boolean

}