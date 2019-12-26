package util

import android.os.SystemClock
import android.view.View
//import com.example.mvvmlib.base.BaseResult
//import com.example.mvvmlib.net.ResponseThrowable
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flowOn
//import kotlinx.coroutines.flow.map

//@ExperimentalCoroutinesApi
//fun <T> Flow<BaseResult<T>>.applyTransform(): Flow<T> {
//    return this
//        .flowOn(Dispatchers.IO)
//        .map {
//            if (it.isSuccess()) return@map it.data
//            else throw ResponseThrowable(it.errorCode, it.errorMsg)
//        }
//}

fun View.clickWithDebounce(debounceTime: Long = 500L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}