package com.ist.httplib.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.ist.httplib.LitepalManager;
import com.ist.httplib.OtaLibConfig;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhengshaorui
 * Time on 2018/10/17
 */

public class RxUtils {
    /**
     * 封装线程调度
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T,T> rxScheduers(){
        return new ObservableTransformer<T,T>(){

            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 判断是否有网络
     * @return
     */
    public static boolean isNetworkPositive() {
        ConnectivityManager connectivityManager = (ConnectivityManager) OtaLibConfig.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWIFIC = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        boolean isETHERNETC = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).isConnected();
        if (isWIFIC || isETHERNETC){
            return true;
        }
        return false;
    }

    public static void deleteDbFile(){
        Observable.just("1")
                .compose(RxUtils.<String>rxScheduers())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (OtaLibConfig.getBuilder().isUsbDb()) {
                            LitepalManager.getInstance().deleteall();
                        }
                        File file = new File(OtaLibConfig.getBuilder().getFilePath(),
                                OtaLibConfig.getBuilder().getFileName());
                        if (file.exists()){
                            file.delete();
                        }
                    }
                });
    }
}
