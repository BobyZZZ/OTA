package com.ist.httplib.mvp.base;

import com.ist.httplib.net.NetErrorMsg;

/**
 * Created by zhengshaorui
 * time: 2018/8/19
 */

public interface IBaseView {
    void error(NetErrorMsg status, String errorMsg);
}
