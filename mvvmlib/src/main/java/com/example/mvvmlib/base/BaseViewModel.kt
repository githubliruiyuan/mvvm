package com.example.mvvmlib.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.viewModelScope
import com.example.mvvmlib.event.Message
import com.example.mvvmlib.event.SingleLiveEvent
import com.example.mvvmlib.net.ExceptionHandle
import com.example.mvvmlib.net.ResponseThrowable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class BaseViewModel(application: Application) : AndroidViewModel(application),
    LifecycleObserver {

    val defUI by lazy { UIChange() }

    /**
     * 所有网络请求都在 viewModelScope 域中启动，当页面销毁时会自动
     * 调用ViewModel的  #onCleared 方法取消所有协程
     */
    fun launchUI(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch { block() }
    }

    /**
     * 用流的方式进行网络请求
     */
    fun <T> launchFlow(block: suspend () -> T): Flow<T> {
        return flow {
            emit(block())
        }
    }

    private var debounceJob: Job? = null

    fun debounce(
        waitMs: Long = 300L,
        destinationFunction: () -> Unit
    ) {
        debounceJob?.cancel()
        debounceJob = launchUI {
            delay(waitMs)
            destinationFunction()
        }
    }

//    fun <T> debounce(
//        waitMs: Long = 300L,
//        destinationFunction: (T) -> Unit
//    ): (T) -> Unit {
//        return { param: T ->
//            debounceJob?.cancel()
//            debounceJob = launchUI {
//                delay(waitMs)
//                destinationFunction(param)
//            }
//        }
//    }
//
//    private var throttleJob: Job? = null
//
//    fun <T> throttleLatest(
//        intervalMs: Long = 300L,
//        destinationFunction: (T) -> Unit
//    ): (T) -> Unit {
//        var latestParam: T
//        return { param: T ->
//            latestParam = param
//            if (throttleJob?.isCompleted != false) {
//                throttleJob = launchUI {
//                    delay(intervalMs)
//                    latestParam.let(destinationFunction)
//                }
//            }
//        }
//    }
//
//    fun <T> throttleFirst(
//        skipMs: Long = 300L,
//        destinationFunction: (T) -> Unit
//    ): (T) -> Unit {
//        return { param: T ->
//            if (throttleJob?.isCompleted != false) {
//                throttleJob = launchUI {
//                    destinationFunction(param)
//                    delay(skipMs)
//                }
//            }
//        }
//    }

    /**
     *  不过滤请求结果
     * @param block 请求体
     * @param error 失败回调
     * @param complete  完成回调（无论成功失败都会调用）
     * @param isShowDialog 是否显示加载框
     */
    fun launch(
        block: suspend CoroutineScope.() -> Unit,
        error: suspend CoroutineScope.(ResponseThrowable) -> Unit = {
            defUI.toastEvent.postValue("${it.code}:${it.errMsg}")
        },
        complete: suspend CoroutineScope.() -> Unit = {},
        isShowDialog: Boolean = true
    ) {
        if (isShowDialog) defUI.showDialog.call()
        launchUI {
            handleException(
                withContext(Dispatchers.IO) { block },
                { error(it) },
                {
                    defUI.dismissDialog.call()
                    complete()
                }
            )
        }
    }

    /**
     * 过滤请求结果，其他全抛异常
     * @param block 请求体
     * @param success 成功回调
     * @param error 失败回调
     * @param complete  完成回调（无论成功失败都会调用）
     * @param isShowDialog 是否显示加载框
     */
    fun <T> launchOnlyresult(
        block: suspend CoroutineScope.() -> BaseResult<T>,
        success: (T) -> Unit,
        error: (ResponseThrowable) -> Unit = {
            defUI.toastEvent.postValue("${it.code}:${it.errMsg}")
        },
        complete: () -> Unit = {},
        isShowDialog: Boolean = true
    ) {
        if (isShowDialog) defUI.showDialog.call()
        launchUI {
            handleException(
                { withContext(Dispatchers.IO) { block() } },
                { res ->
                    executeResponse(res) { success(it) }
                },
                {
                    error(it)
                },
                {
                    defUI.dismissDialog.call()
                    complete()
                }
            )
        }
    }

    /**
     * 请求结果过滤
     */
    private suspend fun <T> executeResponse(
        response: BaseResult<T>,
        success: suspend CoroutineScope.(T) -> Unit
    ) {
        coroutineScope {
            if (response.isSuccess()) success(response.data)
            else throw ResponseThrowable(response.errorCode, response.errorMsg)
        }
    }

    /**
     * 异常统一处理
     */
    private suspend fun <T> handleException(
        block: suspend CoroutineScope.() -> BaseResult<T>,
        success: suspend CoroutineScope.(BaseResult<T>) -> Unit,
        error: suspend CoroutineScope.(ResponseThrowable) -> Unit,
        complete: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                success(block())
            } catch (e: Throwable) {
                error(ExceptionHandle.handleException(e))
            } finally {
                complete()
            }
        }
    }


    /**
     * 异常统一处理
     */
    private suspend fun handleException(
        block: suspend CoroutineScope.() -> Unit,
        error: suspend CoroutineScope.(ResponseThrowable) -> Unit,
        complete: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                block()
            } catch (e: Throwable) {
                error(ExceptionHandle.handleException(e))
            } finally {
                complete()
            }
        }
    }


    /**
     * UI事件
     */
    inner class UIChange {
        val showDialog by lazy { SingleLiveEvent<String>() }
        val dismissDialog by lazy { SingleLiveEvent<Void>() }
        val toastEvent by lazy { SingleLiveEvent<String>() }
        val msgEvent by lazy { SingleLiveEvent<Message>() }
    }

}