package com.chtima.wallettracker.models;

public interface DialogObserver<T> {

    void onSuccess(T obj);
    void onCancel();
    default void onError(Throwable e){};

}
