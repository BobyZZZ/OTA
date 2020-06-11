package com.ist.httplib.bean;

/**
 * Created by zhengshaorui
 * Time on 2018/9/12
 */

public class BaseOtaRespone<T> {
    public String statusCode;
    public T appVersion;

    @Override
    public String toString() {
        return "BaseOtaRespone{" +
                "statusCode='" + statusCode + '\'' +
                ", appVersion=" + appVersion +
                '}';
    }
}
