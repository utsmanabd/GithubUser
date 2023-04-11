package com.everybodv.githubuser.data.remote.retrofit

import com.everybodv.githubuser.BuildConfig
import com.everybodv.githubuser.data.remote.response.GithubDetailResponse
import com.everybodv.githubuser.data.remote.response.GithubUserResponse
import com.everybodv.githubuser.data.remote.response.ItemsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    @GET("search/users")
    fun getUser(
        @Query("q") query:String
    ): Call<GithubUserResponse>

    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    @GET("users/{id}")
    fun getDetail(
        @Path("id") login: String
    ): Call<GithubDetailResponse>

    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    @GET("users/{username}/followers")
    fun getFollowers(
        @Path("username") username: String
    ): Call<List<ItemsItem>>

    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    @GET("users/{username}/following")
    fun getFollowing(
        @Path("username") username: String
    ): Call<List<ItemsItem>>
}