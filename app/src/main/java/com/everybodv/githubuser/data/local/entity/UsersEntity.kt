package com.everybodv.githubuser.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "users")
@Parcelize
data class UsersEntity (
    @field:ColumnInfo(name = "username")
    @field:PrimaryKey
    val login: String,

    @field:ColumnInfo(name = "avatar_url")
    val avatarUrl: String,

    @field:ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean
) : Parcelable