package com.ist.otaservice.pop;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.ist.otaservice.R;


/**
 * Created by zhengshaorui
 * Time on 2018/10/21
 */

public class MsgDialog extends BaseDialog{

    public MsgDialog(Context context) {
        super(context);
    }



    public MsgDialog setTitle(int titleId){
        mCusDialog.setTextView(R.id.common_dialog_title,titleId);
        return this;
    }

    public MsgDialog setMsg(int msgId){
        mCusDialog.setTextView(R.id.common_dialog_tv,msgId);
        return this;
    }

    public MsgDialog setMsg(String msg){
        mCusDialog.setTextView(R.id.common_dialog_tv,msg);
        return this;
    }

    public MsgDialog setPositive(int positiveId, @Nullable final View.OnClickListener listener){
        mCusDialog.setOnClickListener(positiveId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                mCusDialog.dismiss();
            }
        });
        return this;
    }

    public MsgDialog setNegative(int negativeId, @Nullable final View.OnClickListener listener){
        mCusDialog.setOnClickListener(negativeId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                mCusDialog.dismiss();
            }
        });
        return this;
    }

    public CusDialog getDialog(){
        return mCusDialog;
    }





}
