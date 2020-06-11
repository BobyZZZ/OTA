package com.ist.otaservice.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ist.httplib.mvp.base.BasePresent;
import com.ist.httplib.mvp.base.IBaseView;

/**
 * Created by zhengshaorui
 * Time on 2018/9/15
 */

public abstract class BaseFragment<T extends BasePresent> extends Fragment implements IBaseView {
    private static final String TAG = "BaseFragment";
    protected Context mContext;
    protected Activity mActivity;
    protected ProgressDialog mDialog;
    private T mPresent;
    private BackHandleInterface mBackHandleInterface;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof BackHandleInterface){
            mBackHandleInterface = (BackHandleInterface) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(getLayoutId(), container, false);
        mContext = getContext();
        mPresent = getPresent();
        if (mPresent != null){
            mPresent.attachView(this);
        }
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBackHandleInterface.onSelectedFragment(this);

    }

    public abstract int getLayoutId();
    public abstract T getPresent();
    public abstract void initView(View view);
    public abstract boolean onPressBack();



    @Override
    public void onDestroyView() {
        if (mPresent != null){
            mPresent.detachView();
            mPresent = null;
        }
        super.onDestroyView();

    }

    protected void showProgressDialog(int resid) {
        mDialog = ProgressDialog.show(mContext, null, getString(resid), true, true);
        mDialog.setCancelable(false);
        mDialog.show();

    }
    protected void showProgressDialog(String msg) {
        mDialog = ProgressDialog.show(mContext, null, msg, true, true);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    protected void dismissProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }




}
