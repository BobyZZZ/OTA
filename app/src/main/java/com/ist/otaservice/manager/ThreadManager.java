package com.ist.otaservice.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhengshaorui
 * Time on 2018/9/12
 */

public class ThreadManager {
    private static ExecutorService sSingleService = Executors.newSingleThreadExecutor();

    private static class Holder {
        static final ThreadManager INSTANCE = new ThreadManager();
    }

    public static ThreadManager getInstance() {
        return Holder.INSTANCE;
    }

    private ThreadManager() {
    }

    public void execute(Runnable runnable){
        sSingleService.execute(runnable);
    }


}
