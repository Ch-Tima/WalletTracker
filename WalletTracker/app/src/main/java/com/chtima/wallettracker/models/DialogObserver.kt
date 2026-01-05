package com.chtima.wallettracker.models

fun interface DialogObserver<T>{
    fun onSuccess(result: T)
    fun onCancel() {}
}