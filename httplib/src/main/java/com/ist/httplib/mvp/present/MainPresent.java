package com.ist.httplib.mvp.present;

import android.util.Log;

import com.ist.httplib.bean.BaseOtaRespone;
import com.ist.httplib.mvp.base.BasePresent;
import com.ist.httplib.mvp.contract.OtaContract;
import com.ist.httplib.mvp.model.MainModel;
import com.ist.httplib.net.HttpTaskManager;
import com.ist.httplib.net.NetErrorMsg;
import com.ist.httplib.net.callback.UpdateListenerAdapter;

/**
 * Created by zhengshaorui
 * Time on 2018/9/26
 */

public class MainPresent extends BasePresent<OtaContract.IMainView> {

    private OtaContract.IMainView mIView;
    private final MainModel mMainModel;

    public static MainPresent create(OtaContract.IMainView iMainView){
        return new MainPresent(iMainView);
    }
    private MainPresent(OtaContract.IMainView iMainView){
        mIView = iMainView;
        mMainModel = MainModel.create(mIView);

    }

    public void checkLocalUpdate(){
        mMainModel.checkLocalUpdate();
    }

    public void checkNetUpdate(){
        Log.e("Keven", "checkNetUpdate: *****");
        HttpTaskManager.getInstance().checkUpdate(new UpdateListenerAdapter() {
            @Override
            public void checkUpdate(boolean isCanUpdate, BaseOtaRespone respone) {
                super.checkUpdate(isCanUpdate, respone);
                mIView.checkNetUpdate(isCanUpdate,respone);
            }

            @Override
            public void error(NetErrorMsg status, String errorMsg) {
                super.error(status, errorMsg);
                Log.e("Keven", "error: errorMsg =="+errorMsg );
                mIView.error(status,errorMsg);
            }
        });


    }
}
