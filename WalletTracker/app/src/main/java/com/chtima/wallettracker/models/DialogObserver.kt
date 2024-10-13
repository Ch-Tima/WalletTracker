package com.chtima.wallettracker.models

interface DialogObserver<T>{
    fun onSuccess(result: T)
    fun onCancel() {}
}