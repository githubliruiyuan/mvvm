package com.example.mvvm.app.main

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.mvvm.BR
import com.example.mvvm.R
import com.example.mvvm.app.main.model.CountState
import com.example.mvvm.app.main.vm.MainVM
import com.example.mvvm.databinding.ActivityMainBinding
import com.example.mvvmlib.base.BaseActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import util.clickWithDebounce

class MainActivity : BaseActivity<MainVM, ActivityMainBinding>(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    @ExperimentalCoroutinesApi
    override fun initialize(savedInstanceState: Bundle?) {
        binding?.setVariable(BR.vm, viewModel)
        binding?.setVariable(BR.listener, this)

        binding?.testClick?.clickWithDebounce {
            Log.d("test", "click")
        }

        viewModel.debounceEdit(500, binding?.edit!!)
        viewModel.countState.postValue(CountState())
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
