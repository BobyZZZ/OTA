package com.ist.otaservice.pop;

import android.content.Context;
import android.view.ViewGroup;

import com.ist.otaservice.MainApplication;
import com.ist.otaservice.R;


/**
 * Created by zhengshaorui
 * Time on 2018/10/21
 */

public abstract class BaseDialog {
    protected static final int DIALOG_WIDTH = MainApplication.CONTEXT
            .getResources().getDimensionPixelSize(R.dimen.x550);
    protected Context mContext;
    protected CusDialog mCusDialog;

    public BaseDialog(Context context) {
        mContext = context;
        mCusDialog = new CusDialog.Builder()
                .setContext(context)
                .setLayoutId(R.layout.common_msg)
                .setWidth(getWidth() == -1?DIALOG_WIDTH:getWidth())
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOutSideDimiss(false)
                .builder();


    }






    protected int getWidth() {

        return -1;
    }




}
