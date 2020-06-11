package com.ist.otaservice;

import android.os.Environment;

/**
 * Created by zhengshaorui
 * Time on 2018/10/17
 */

public class Constant {
    public static final String ACTION_BACKGROUND_NET_UPDATE = "backgound_net_update";
    public static final int THREAD_COUNT = 6;//设定多少个线程去下载，最大不能超过8个
    public static final String START_DOWNLOAD = "start_download";
    //默认路径
    public static final String SAVE_PATH = "/cache";//cache分区不够，不能下载
    //默认名字
    public static final String FILE_NAME = "update.zip";


    public static String ROOTPATH = "/mnt/usb";

    // 1s 保存一次
    public static final long SAVE_TIME = 1000;
}
