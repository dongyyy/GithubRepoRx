package kr.mbch.githubreporx

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.mbch.githubreporx.retrofit.GithubRepository
import kr.mbch.githubreporx.retrofit.entity.GithubRepo

class GithubReposViewModel {
    private val repository = GithubRepository()

    var searchText = ""

    fun searchGithubRepos(search: String): Single<List<GithubRepo>> {
        searchText = search
        //서버에 api 콜하고 응답을 한번만 받으므로 Single.create 사용
        //onSuccess 만 구현
        return Single.create<List<GithubRepo>> { emitter ->
            repository.searchGithubRepos(searchText)
                .subscribe({
                    Completable.merge(
                        it.map { repo ->
                            checkStar(repo.owner.userName, repo.name)
                                .doOnComplete { repo.star = true }
                                .onErrorComplete()
                        }
                    ).subscribe {
                        emitter.onSuccess(it)
                    }
                }, {})
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun checkStar(owner: String, repo: String): Completable =
        repository.checkStar(owner, repo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}
