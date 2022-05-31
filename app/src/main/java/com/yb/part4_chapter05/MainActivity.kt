package com.yb.part4_chapter05

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yb.part4_chapter05.data.database.DatabaseProvider
import com.yb.part4_chapter05.data.entity.GithubOwner
import com.yb.part4_chapter05.data.entity.GithubRepoEntity
import com.yb.part4_chapter05.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var activityMainBinding: ActivityMainBinding
    private val job = Job()

    private val repositoryDao by lazy { DatabaseProvider.provideDB(applicationContext).repositoryDao() }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        initViews()

        launch {
            addMockData()

            val githubRepositories = loadGithubRepositories()

            withContext(coroutineContext) {
                Log.d("repositories", githubRepositories.toString())
            }
        }

    }

    private fun initViews() = with(activityMainBinding){
        searchButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))

        }


    }

    private suspend fun addMockData() = withContext(Dispatchers.IO) {
        val mockData = (0 until 10).map {
            GithubRepoEntity(
                name = "repo $it",
                fullName = "name $it",
                owner = GithubOwner("login", "avatarUrl"),
                description = null,
                language = null,
                updateAt = Date().toString(),
                stargazersCount = it
            )
        }

        repositoryDao.insertAll(mockData)
    }

    private suspend fun loadGithubRepositories() = withContext(Dispatchers.IO) {
        return@withContext repositoryDao.getAllRepositories()
    }
}