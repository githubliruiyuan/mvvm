package com.example.mvvm.app.main

import android.os.Bundle
import com.example.mvvm.R
import com.example.mvvm.databinding.ActivityTestBinding
import com.example.mvvmlib.base.BaseActivity
import com.example.mvvmlib.base.BaseViewModel
import com.example.mvvmlib.channel.send
import kotlinx.coroutines.ExperimentalCoroutinesApi

class TestActivity : BaseActivity<BaseViewModel, ActivityTestBinding>() {

    override fun layoutId(): Int {
        return R.layout.activity_test
    }

    @ExperimentalCoroutinesApi
    override fun initialize(savedInstanceState: Bundle?) {
        send("event is @@@")

        binding?.test?.setOnClickListener {
            send("event is click")
        }
    }

}