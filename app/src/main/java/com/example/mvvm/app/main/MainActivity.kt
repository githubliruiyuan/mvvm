package com.example.mvvm.app.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvm.BR
import com.example.mvvm.R
import com.example.mvvm.app.main.model.CountState
import com.example.mvvm.app.main.vm.MainVM
import com.example.mvvm.databinding.ActivityMainBinding
import com.example.mvvmlib.base.BaseActivity
import com.example.mvvmlib.channel.receive
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import util.clickWithDebounce

class MainActivity : BaseActivity<MainVM, ActivityMainBinding>(), View.OnClickListener {

    companion object {
        private val TITLES = arrayOf(
//            "Android",
//            "Beta",
//            "Cupcake",
//            "Donut",
//            "Eclair",
//            "Froyo",
//            "Gingerbread",
//            "Honeycomb",
//            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow",
            "Nougat",
            "Oreo"
        )
    }

    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    @ExperimentalCoroutinesApi
    override fun initialize(savedInstanceState: Bundle?) {
        binding?.setVariable(BR.vm, viewModel)
        binding?.setVariable(BR.listener, this)

        binding?.testClick?.clickWithDebounce {
            Log.d("test", "click")
            startActivity(Intent(this, TestActivity::class.java))
        }

        viewModel.debounceEdit(500, binding?.edit!!)
        viewModel.countState.postValue(CountState())


        val firstAdapter = HorizontalAdapter(TITLES)
        val firstRecyclerView = findViewById<MultiSnapRecyclerView>(R.id.first_recycler_view)
        val firstManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        firstRecyclerView.layoutManager = firstManager
        firstRecyclerView.adapter = firstAdapter

    }

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("xxxx", "onCreate")

        receive<String>(true) {
            Log.d("xxxx", "active true 接受到事件 = $it")
        }

        receive<String> {
            Log.d("xxxx", "active false 接受到事件 = $it")
        }.catch {
            // 当作用域中发生异常
            // it 为异常对象
        }.finally {
            // 无论正常或者异常结束后回调, it表示异常对象, 如果为null表示正常结束
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("xxxx", "onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("xxxx", "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("xxxx", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("xxxx", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("xxxx", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("xxxx", "onDestroy")
    }


    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onClick(view: View?) {
        when (view?.id) {
//            R.id.hello -> {
//                viewModel.getFirstData()
//            }
            R.id.count -> {
                viewModel.countDown()
                viewModel.mergeData()
            }
        }
    }

}
