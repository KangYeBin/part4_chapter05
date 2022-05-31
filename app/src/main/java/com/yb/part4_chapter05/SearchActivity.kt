package com.yb.part4_chapter05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.yb.part4_chapter05.data.entity.GithubRepoEntity
import com.yb.part4_chapter05.databinding.ActivitySearchBinding
import com.yb.part4_chapter05.utility.RetrofitUtil
import com.yb.part4_chapter05.view.RepositoryRecyclerAdapter
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity(), CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var activitySearchBinding: ActivitySearchBinding

    private lateinit var repositoryRecyclerAdapter: RepositoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(activitySearchBinding.root)

        initAdapter()
        initViews()
        bindViews()
    }

    private fun initAdapter() = with(activitySearchBinding) {
        repositoryRecyclerAdapter = RepositoryRecyclerAdapter()
        searchRecyclerView.layoutManager = LinearLayoutManager(this@SearchActivity)
    }

    private fun initViews() = with(activitySearchBinding) {
        emptyResultTextView.isGone = true
        searchRecyclerView.adapter = repositoryRecyclerAdapter

    }

    private fun bindViews() = with(activitySearchBinding) {
        searchButton.setOnClickListener {
            searchKeyword(searchInputEditText.text.toString())

        }

    }

    private fun searchKeyword(keyword: String) = launch {
        withContext(Dispatchers.IO) {
            val response = RetrofitUtil.githubApiService.searchRepositories(keyword)

            if (response.isSuccessful) {
                val body = response.body()

                withContext(Dispatchers.Main) {
                    body?.let {
                        Log.d("response", body.items.toString())

                        setData(it.items)

                    }

                }
            }
        }

    }

    private fun setData(items: List<GithubRepoEntity>) {
        repositoryRecyclerAdapter.setRepositoryList(items) {
            Toast.makeText(this, "entity : $it", Toast.LENGTH_SHORT).show()
        }
    }
}