package com.example.mvvm.net

import com.example.mvvm.BuildConfig
import com.example.mvvmlib.net.interceptor.Level
import com.example.mvvmlib.net.interceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitProvider {

    private var mRetrofit: Retrofit? = null
    private var mClient: OkHttpClient? = null

    companion object {
        val instance: RetrofitProvider by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitProvider()
        }

        private const val BASE_URL = "https://www.wanandroid.com/"
        private const val DEFAULT_CONNECT_TIMEOUT = 20L
        private const val DEFAULT_READ_TIMEOUT = 20L
    }

    init {
        val builder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
            .addNetworkInterceptor(LoggingInterceptor().apply {
                isDebug = BuildConfig.DEBUG
                level = Level.BASIC
                type = Platform.INFO
                requestTag = "Request"
                requestTag = "Response"
            })


        mClient = builder.build()

        mRetrofit = Retrofit.Builder()
            .client(mClient!!)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(apiClass: Class<T>): T {
        return mRetrofit!!.create(apiClass)
    }

    fun getOkHttpClient(): OkHttpClient? {
        return mClient
    }

}