package com.ist.httplib.bean;


import com.ist.httplib.net.callback.UpdateListener;

/**
 * Created by zhengshaorui
 * Time on 2018/9/12
 */

public class DownloadBean {
    public String fileUrl;
    public String fileName;
    public String filePath;
    public long fileLength;
    public int threadCount;
    public UpdateListener listener;

}
