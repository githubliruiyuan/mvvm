package com.example.mvvmlib.base

data class BaseResult<out T>(val errorMsg: String, val errorCode: Int, val data: T) {
    fun isSuccess() = errorCode == 0
}