package com.yb.part4_chapter05

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import com.yb.part4_chapter05.databinding.ActivitySignInBinding
import com.yb.part4_chapter05.utility.AuthTokenProvider
import com.yb.part4_chapter05.utility.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SignInActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var activitySignInBinding: ActivitySignInBinding
    private val authTokenProvider by lazy { AuthTokenProvider(this) }

    var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(activitySignInBinding.root)

        if (checkAuthCodeExist()) {
            launchMainActivity()
        } else {
            initViews()
        }
    }

    private fun launchMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })

    }

    private fun initViews() = with(activitySignInBinding) {
        loginButton.setOnClickListener {
            loginGithub()
        }
    }

    private fun checkAuthCodeExist(): Boolean = authTokenProvider.token.isNullOrEmpty().not()


    private fun loginGithub() {
        val loginUri = Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .build()

        //CustomTabsIntent로 현재 화면에서 CustomTab으로 이동할수있는  Intent 객체를 생성
        CustomTabsIntent.Builder().build().also {
            it.launchUrl(this, loginUri)
        }
    }

    //Intent를 받아서 실행

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("data", intent?.data.toString())

        intent?.data?.getQueryParameter("code")?.let {
            Log.d("data", intent.data.toString())
            //TODO Access Token 가져오기
            launch(coroutineContext) {
                showProgress()
                getAccessToken(it)
                dismissProgress()
            }
        }
    }

    private suspend fun showProgress() = withContext(coroutineContext) {
        with(activitySignInBinding) {
            loginButton.isGone = true
            progressBar.isGone = false
            progressTextView.isGone = false
        }
    }

    private suspend fun dismissProgress() = withContext(coroutineContext) {
        with(activitySignInBinding) {
            loginButton.isGone = false
            progressBar.isGone = true
            progressTextView.isGone = true
        }
    }

    private suspend fun getAccessToken(code: String) = withContext(Dispatchers.IO) {
        val response = RetrofitUtil.githubApiService.getAccessToken(
            clientId = BuildConfig.GITHUB_CLIENT_ID,
            clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
            code = code
        )

        if (response.isSuccessful) {
            val accessToken = response.body()?.accessToken ?: ""
            Log.d("accessToken", accessToken.toString())

            if (accessToken.isNotEmpty()) {
                authTokenProvider.updateToken(accessToken)
            } else {
                Toast.makeText(this@SignInActivity, "accessToken이 존재하지 않습니다", Toast.LENGTH_SHORT)
                    .show()
            }

        }

    }
}