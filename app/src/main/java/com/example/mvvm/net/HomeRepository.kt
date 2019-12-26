package com.example.mvvm.net

import com.example.mvvm.net.model.HomeListBean
import com.example.mvvm.net.model.NavTypeBean
import com.example.mvvm.net.model.UsedWeb
import com.example.mvvmlib.base.BaseResult

class HomeRepository private constructor(
    private val network: HomeNetwork
//    private val localData: LocalData   可用 Room 等 来做本地缓存，Demo 只展示了从网络获取
) {

//    suspend fun getBannerData(): BaseResult<List<BannerBean>> {
//        return network.getBannerData()
//    }

    suspend fun getHomeList(page: Int): BaseResult<HomeListBean> {
        return network.getHomeList(page)
    }

    suspend fun getNaviJson(): BaseResult<List<NavTypeBean>> {
        return network.getNaviJson()
    }

    suspend fun getProjectList(page: Int, cid: Int): BaseResult<HomeListBean> {
        return network.getProjectList(page, cid)
    }

//    suspend fun getPopularWeb(): BaseResult<List<UsedWeb>> {
//        return network.getPopularWeb()
//    }

    companion object {

        @Volatile
        private var INSTANCE: HomeRepository? = null

        fun instance(network: HomeNetwork) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HomeRepository(network).also { INSTANCE = it }
            }
    }
}