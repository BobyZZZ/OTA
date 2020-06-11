package com.ist.httplib.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Created by zhengshaorui
 * Time on 2018/9/12
 */

public class ThreadBean extends LitePalSupport{
    public int theadId;
    public String url;
    public String name;
    public long startPos;
    public long endPos;
    public long fileLength; //文件的长度
    public long threadLength; //单个线程文件长度
}
