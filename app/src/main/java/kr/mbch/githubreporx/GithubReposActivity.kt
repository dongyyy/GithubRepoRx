package kr.mbch.githubreporx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_github_repos.*
import kr.mbch.githubreporx.retrofit.entity.GithubRepo
import java.util.concurrent.TimeUnit

class GithubReposActivity : AppCompatActivity() {

    private val viewModel: GithubReposViewModel by lazy {
        GithubReposViewModel()
    }
    private val adapter: GithubReposAdapter by lazy {
        GithubReposAdapter()
    }

    val searchSubject = PublishSubject.create<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_repos)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.adapter

        refreshLayout.setOnRefreshListener { searchLoad(viewModel.searchText, false) }

        subscribeSearchSubscribe() //PublishSubject 구독

        searchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                searchSubject.onNext(text.toString()) //키보드 눌릴 때마다 onNext
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    private fun subscribeSearchSubscribe() {
        searchSubject
            .debounce(400, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                searchLoad(it, true)
            }
    }

    private fun searchLoad(search: String, showLoading: Boolean) {
        if (showLoading)
            showLoading()
        viewModel.searchGithubRepos(search)
            .subscribe(object : DisposableSingleObserver<List<GithubRepo>>() {
                override fun onSuccess(t: List<GithubRepo>) {
                    hideLoading()
                    adapter.items = t
                }

                override fun onError(e: Throwable) {
                    hideLoading()
                }
            })
    }

    private fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loading.visibility = View.GONE
        refreshLayout.isRefreshing = false
    }

    }