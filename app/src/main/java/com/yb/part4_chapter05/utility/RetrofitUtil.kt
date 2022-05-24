package com.yb.part4_chapter05.utility

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.yb.part4_chapter05.BuildConfig
import com.yb.part4_chapter05.data.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitUtil {

    val githubApiService: AuthApiService by lazy {
        getGithubAuthRetrofit().create(AuthApiService::class.java)
    }

    private fun getGithubAuthRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Url.GITHUB_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create())
            )
            .client(buildOkHttpClient())
            .build()

    }

    private fun buildOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }
}