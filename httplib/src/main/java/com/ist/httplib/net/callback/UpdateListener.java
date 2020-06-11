package com.ist.httplib.net.callback;


import com.ist.httplib.bean.BaseOtaRespone;
import com.ist.httplib.net.NetErrorMsg;

/**
 * Created by zhengshaorui
 * Time on 2018/9/12
 */

public interface UpdateListener {
    void checkUpdate(boolean isCanUpdate, BaseOtaRespone respone);
    void complete(String fileName);
    void doProgress(int progress, long currentSize, long totalSize);
    void error(NetErrorMsg status, String errorMsg);
}
