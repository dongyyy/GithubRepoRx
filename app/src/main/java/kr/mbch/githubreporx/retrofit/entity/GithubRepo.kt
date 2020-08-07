package kr.mbch.githubreporx.retrofit.entity

import com.google.gson.annotations.SerializedName

data class GithubRepo(
    val name: String,
    val description: String,
    @SerializedName("owner")
    val owner: User,
    var star: Boolean = false
)
