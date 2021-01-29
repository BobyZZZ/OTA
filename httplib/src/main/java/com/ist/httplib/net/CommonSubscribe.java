package com.ist.httplib.net;

import android.util.Log;

import com.ist.httplib.InvokeManager;
import com.ist.httplib.bean.BaseOtaRespone;
import com.ist.httplib.bean.customer.AppVersion;
import com.ist.httplib.net.callback.UpdateListenerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.observers.ResourceObserver;

public abstract class CommonSubscribe<T extends BaseOtaRespone> extends ResourceObserver<T> {
    private static final String TAG = "CommonSubscribe";
    private UpdateListenerAdapter mView;

    public CommonSubscribe(UpdateListenerAdapter iview) {
        mView = iview;

    }


    @Override
    public void onNext(T t) {
        /*SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Log.e("Keven", "onNext: format =="+format);
        String buildDateLocal="";
        try{
            buildDateLocal =  format.format(new Date(Long.parseLong(InvokeManager.get("ro.bootimage.build.date.utc", "1576919867")))).toString();
        }catch (Exception e){
            e.printStackTrace();
        }*/


        /*if (t.customID.equals(deviceBean.customID)){
            Log.d(TAG, "zsr --> 服务器: "+t.version+" / 本地: "+deviceBean.version);
            int status  = t.version.compareToIgnoreCase(deviceBean.version);
            if (status > 0){
                onResponse(t);
            }else{
                mView.checkUpdate(false,deviceBean);
            }

        }else{
            mView.error(NetErrorMsg.ID_NOT_SAME,"customID not the same");
        }*/

       /* BaseOtaRespone<AppVersion> deviceBean = new BaseOtaRespone<>();

        deviceBean.appVersion.buildDate = buildDateLocal;
        Log.e("Keven", "onNext: buildDateLocal =="+buildDateLocal);
        if(Long.parseLong(buildDateLocal)<Long.parseLong(t.appVersion.buildDate)){
            onResponse(t);
        }else{
            mView.checkUpdate(false,deviceBean);
        }*/
        if (t.appVersion == null) {
            Log.d(TAG, "zyc -> onNext t.appVersion: " + t);
            if (mView != null) {
                mView.error(NetErrorMsg.OTHERS,"t.appVersion is null");
            }
        } else {
            onResponse(t);
        }
    }

    public abstract  void onResponse(T t);

    @Override
    public void onError(Throwable e) {
        if (mView == null){
            return;
        }
        Log.d(TAG, "zsr --> onError: "+e.toString());
        mView.error(NetErrorMsg.SERVER_NOT_FOUND,e.toString());



    }

    @Override
    public void onComplete() {

    }

}