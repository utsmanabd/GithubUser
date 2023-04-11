package com.everybodv.githubuser.data.remote.response

import com.google.gson.annotations.SerializedName

data class GithubDetailResponse(
	@field:SerializedName("login")
	val login: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("followers")
	val followers: Int,

	@field:SerializedName("following")
	val following: Int,

	@field:SerializedName("avatar_url")
	val avatarUrl: String
)
