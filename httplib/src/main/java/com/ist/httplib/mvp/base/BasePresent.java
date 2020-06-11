package com.ist.httplib.mvp.base;

/**
 * Created by zhengshaorui
 * time: 2018/8/19
 */

public abstract class BasePresent<T> {

    protected T mView;
    public void attachView(T view){
        mView = view;
    }

    public void detachView(){
        mView = null;
    }
}
