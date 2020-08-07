package kr.mbch.githubreporx.retrofit

import io.reactivex.Completable
import io.reactivex.Single
import kr.mbch.githubreporx.retrofit.entity.GithubRepo


class GithubRepository {

    private val api = ApiManager.githubApi

    fun searchGithubRepos(q: String): Single<List<GithubRepo>> =
        api.searchRepos(q)
            .map {
                it.asJsonObject.getAsJsonArray("items")
                    .map { repo ->
                        ApiManager.gson.fromJson(repo, GithubRepo::class.java)!!
                    }
            }

    fun checkStar(owner: String, repo: String): Completable =
        api.checkStar(owner, repo)
}
