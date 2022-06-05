package com.yb.part4_chapter05

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.yb.part4_chapter05.data.database.DatabaseProvider
import com.yb.part4_chapter05.data.entity.GithubOwner
import com.yb.part4_chapter05.data.entity.GithubRepoEntity
import com.yb.part4_chapter05.databinding.ActivityMainBinding
import com.yb.part4_chapter05.view.RepositoryRecyclerAdapter
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var repositoryRecyclerAdapter: RepositoryRecyclerAdapter
    private val job = Job()

    private val repositoryDao by lazy {
        DatabaseProvider.provideDB(applicationContext).repositoryDao()
    }


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        initAdapter()
        initViews()

    }

    private fun initAdapter() = with(activityMainBinding) {
        repositoryRecyclerAdapter = RepositoryRecyclerAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
    }

    private fun initViews() = with(activityMainBinding) {
        recyclerView.adapter = repositoryRecyclerAdapter

        searchButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()

        launch(coroutineContext) {
            loadLikedRepositoryList()
        }

    }

    private suspend fun loadLikedRepositoryList() = with(Dispatchers.IO) {
        val repoList = repositoryDao.getAllRepositories()

        withContext(Dispatchers.Main) {
            setData(repoList)
        }

    }

    private fun setData(githubRepositoryList: List<GithubRepoEntity>) = with(activityMainBinding) {
        if (githubRepositoryList.isEmpty()) {
            emptyResultTextView.isGone = false
            recyclerView.isGone = true

        } else {
            emptyResultTextView.isGone = true
            recyclerView.isGone = false
            repositoryRecyclerAdapter.setRepositoryList(githubRepositoryList) {
                startActivity(
                    Intent(this@MainActivity, RepositoryActivity::class.java).apply {
                        putExtra(RepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                        putExtra(RepositoryActivity.REPOSITORY_NAME_KEY, it.name)
                    }
                )
            }
        }

    }


}