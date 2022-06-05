package com.yb.part4_chapter05

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import com.yb.part4_chapter05.data.entity.GithubRepoEntity
import com.yb.part4_chapter05.databinding.ActivityRepositoryBinding
import com.yb.part4_chapter05.extensions.loadCenterInside
import com.yb.part4_chapter05.utility.RetrofitUtil
import kotlinx.coroutines.*
import retrofit2.Retrofit
import kotlin.coroutines.CoroutineContext

class RepositoryActivity : AppCompatActivity(), CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var activityRepositoryBinding: ActivityRepositoryBinding

    companion object {
        const val REPOSITORY_OWNER_KEY = "REPOSITORY_OWNER_KEY"
        const val REPOSITORY_NAME_KEY = "REPOSITORY_NAME_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRepositoryBinding = ActivityRepositoryBinding.inflate(layoutInflater)
        setContentView(activityRepositoryBinding.root)

        val repositoryOwner = intent.getStringExtra(REPOSITORY_OWNER_KEY) ?: kotlin.run {
            toast("Repository Owner 이름이 없습니다")
            finish()
            return
        }

        val repositoryName = intent.getStringExtra(REPOSITORY_NAME_KEY) ?: kotlin.run {
            toast("Repository 이름이 없습니다")
            finish()
            return
        }

        launch {
            loadRepository(repositoryOwner, repositoryName)?.let {
                setData(it)
            } ?: run {
                toast("Repository 정보가 없습니다")
                finish()
            }
        }

        showLoading(true)

    }

    private suspend fun loadRepository(
        repositoryOwner: String,
        repositoryName: String,
    ): GithubRepoEntity? = withContext(coroutineContext) {
        var repositoryEntity: GithubRepoEntity? = null
        withContext(Dispatchers.IO) {
            val response = RetrofitUtil.githubApiService.getRepository(repositoryOwner, repositoryName)

            if (response.isSuccessful) {
                val body = response.body()

                withContext(Dispatchers.Main) {
                    body?.let { repo ->
                        repositoryEntity = repo
                    }
                }

            }
        }
        return@withContext repositoryEntity
    }

    private fun setData(githubRepoEntity: GithubRepoEntity) = with(activityRepositoryBinding) {
        showLoading(false)

        ownerProfileImageView.loadCenterInside(githubRepoEntity.owner.avatarUrl, 42f)
        ownerNameAndRepoNameTextView.text = "${githubRepoEntity.owner.login}/${githubRepoEntity.name}"
        stargazersCountText.text = githubRepoEntity.stargazersCount.toString()
        githubRepoEntity.language?.let { language ->
            languageText.isGone = false
            languageText.text = language
        }?: kotlin.run {
            languageText.isGone = true
            languageText.text = ""
        }
        descriptionTextView.text = githubRepoEntity.description
        updateTimeTextView.text = githubRepoEntity.updateAt

    }

    private fun showLoading(isShown: Boolean) = with(activityRepositoryBinding){
        progressBar.isGone = isShown.not()
    }

    private fun Context.toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


}