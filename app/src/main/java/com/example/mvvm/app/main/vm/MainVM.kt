package com.example.mvvm.app.main.vm

import android.app.Application
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import com.example.mvvm.app.main.model.CountState
import com.example.mvvm.app.main.model.HomeState
import com.example.mvvm.net.HomeNetwork
import com.example.mvvm.net.HomeRepository
import com.example.mvvmlib.base.BaseViewModel
import com.example.mvvmlib.event.SingleLiveEvent
import com.example.mvvmlib.net.ExceptionHandle
import com.example.mvvmlib.net.ResponseThrowable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainVM(application: Application) : BaseViewModel(application) {

    val homeState = SingleLiveEvent<HomeState>()
    val countState = SingleLiveEvent<CountState>()

    private val homeRepository by lazy { HomeRepository.instance(HomeNetwork.instance()) }

    fun getData() {
        launch(block = {
            val list = homeRepository.getNaviJson()
            homeState.postValue(HomeState().also { it.text = list.data[0].name })
        }, error = {
            print(it)
        }, complete = {

        })

    }

    @ExperimentalCoroutinesApi
    fun countDown() {
        launchUI {
            flow {
                (10 downTo 0).forEach {
                    delay(1000)
                    emit("$it s")
                }
            }
                .flowOn(Dispatchers.Default)
                .onStart {
                    // 倒计时开始 ，在这里可以让Button 禁止点击状态
                    countState.postValue(CountState().also {
                        it.clickable = false
                    })
                }
                .onCompletion {
                    // 倒计时结束 ，在这里可以让Button 恢复点击状态
                    countState.postValue(CountState().also {
                        it.text = "重新倒计时"
                        it.clickable = true
                    })
                }
                .collect { str ->
                    // 在这里 更新LiveData 的值来显示到UI
                    countState.postValue(CountState().also {
                        it.text = str
                    })
                }
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun getFirstData() {
        launchUI {
            launchFlow { homeRepository.getNaviJson() }
                .flatMapConcat {
                    return@flatMapConcat if (it.isSuccess()) {
                        launchFlow { homeRepository.getProjectList(0, it.data[0].id) }
                    } else throw ResponseThrowable(it.errorCode, it.errorMsg)
                }
                .onStart { defUI.showDialog.postValue(null) }
                .flowOn(Dispatchers.IO)
                .onCompletion { defUI.dismissDialog.call() }
                .catch {
                    // 错误处理
                    val err = ExceptionHandle.handleException(it)
                    Log.d("getFirstData", "${err.code}: ${err.errMsg}")
                    defUI.toastEvent.postValue("${err.code}:${err.errMsg}")
                }
                .collect { rep ->
                    if (rep.isSuccess()) {
                        homeState.postValue(HomeState().also { it.text = "测试：${rep.data.total}" })
                    }
                }
        }

    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun mergeData() {
//        launchUI {
//            flow {
//                emit(homeRepository.getNaviJson())
//                emit(homeRepository.getHomeList(0))
//            }
//                .onStart { defUI.showDialog.postValue(null) }
//                .flowOn(Dispatchers.IO)
//                .onCompletion { defUI.dismissDialog.call() }
//                .catch {
//                    // 错误处理
//                    val err = ExceptionHandle.handleException(it)
//                    Log.d("mergeData", "${err.code}: ${err.errMsg}")
//                    defUI.toastEvent.postValue("${err.code}:${err.errMsg}")
//                }
//                .collect {
//                    if (it.isSuccess()) {
////                        if (it.data is HomeListBean) {
////                            Log.d("rep2", "${it.data}")
////                        } else {
////                            Log.d("rep1", "${it.data}")
////                        }
//                    }
//                }
//        }

        launchUI {
            val x1 =  async{
                homeRepository.getNaviJson()
            }
            val x2 =  async{
                homeRepository.getHomeList(0)
            }
        }

        (0 until 1000).forEach {
            launchUI {
                withContext(Dispatchers.Default) {
                    Log.e("xxxx", Thread.currentThread().toString() + it)
                }

            }
        }


    }

    @ExperimentalCoroutinesApi
    fun debounceEdit(timeout: Long, textView: TextView) {
        textView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.trim()
                debounce(waitMs = timeout) {
                    homeState.postValue(HomeState().also { it.text = text })
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


}