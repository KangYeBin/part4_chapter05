package com.yb.part4_chapter05

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.yb.part4_chapter05.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var activityMainBinding: ActivityMainBinding

    var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        initViews()

    }

    private fun initViews() = with(activityMainBinding) {
        loginButton.setOnClickListener {
            loginGithub()
        }
    }

    //TODO https://github.com/login/oauth/authorize?client_id=10f7a447fd70224e58f7
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
                getAccessToken(it)
            }
        }
    }

    private suspend fun getAccessToken(code: String) = withContext(Dispatchers.IO){

    }
}