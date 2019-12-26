package com.example.mvvm.net

import com.example.mvvm.net.api.HomeApi

class HomeNetwork {

    private val mService by lazy { RetrofitProvider.instance.create(HomeApi::class.java) }

//    suspend fun getBannerData() = mService.getBanner()

    suspend fun getHomeList(page: Int) = mService.getHomeList(page)

    suspend fun getNaviJson() = mService.naviJson()

    suspend fun getProjectList(page: Int, cid: Int) = mService.getProjectList(page, cid)

//    suspend fun getPopularWeb() = mService.getPopularWeb()


    companion object {
        @Volatile
        private var network: HomeNetwork? = null

        fun instance() = network ?: synchronized(this) {
            network ?: HomeNetwork().also { network = it }
        }
    }

}