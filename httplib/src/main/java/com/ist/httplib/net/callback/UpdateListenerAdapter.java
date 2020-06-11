package com.ist.httplib.net.callback;

import com.ist.httplib.bean.BaseOtaRespone;
import com.ist.httplib.net.NetErrorMsg;

/**
 * Created by zhengshaorui
 * Time on 2018/9/26
 */

public abstract class UpdateListenerAdapter implements UpdateListener{
    @Override
    public void checkUpdate(boolean isCanUpdate, BaseOtaRespone respone) {

    }

    @Override
    public void complete(String fileName) {

    }

    @Override
    public void doProgress(int progress, long currentSize, long totalSize) {

    }

    @Override
    public void error(NetErrorMsg status, String errorMsg) {

    }
}
