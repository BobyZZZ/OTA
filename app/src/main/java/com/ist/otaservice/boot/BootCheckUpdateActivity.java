package com.ist.otaservice.boot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.ist.otaservice.Constant;
import com.ist.otaservice.MainApplication;
import com.ist.otaservice.R;
import com.ist.otaservice.utils.OtaUtil;

import java.io.File;

public class BootCheckUpdateActivity extends Activity {
    private static final String TAG = "BootCheckUpdateActivity";
    private Context mContext;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot_check_update);
        mContext = this;

        if (getIntent() != null && "net".equals(getIntent().getStringExtra("status"))){
            checkNetFile();
        }else {
            checkFile();
        }

    }

    private void checkNetFile() {
        //开始校验升级
        final File file = new File(Constant.SAVE_PATH + File.separator + Constant.FILE_NAME);
        if (file.exists()) {
            //请在这里做校验升级
            showProgressDialog(R.string.check);
            OtaUtil.checkLocalHasUpdateFile(file, new OtaUtil.CheckListener() {
                @Override
                public void check(int progress) {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.setMessage(getString(R.string.check) + "     -->    " + progress + "%");
                    }
                }

                @Override
                public void success() {
                    dismissProgressDialog();
                    OtaUtil.startUpgrade(mContext, file, R.string.local_file_can_upgrade, R.string.remind_me,
                            BootCheckUpdateActivity.this);
                }

                @Override
                public void fail(String errorMsg) {
                    MainApplication.HANDLER.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            Toast.makeText(mContext, getString(R.string.check_your_file), Toast.LENGTH_SHORT).show();
                        }
                    },500);

                }
            });
        } else {
            dismissProgressDialog();
        }
    }

    private void checkFile() {
        final File file = new File(Constant.SAVE_PATH+File.separator+ Constant.FILE_NAME);
        if (file.exists()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(R.string.local_has_file);
            builder.setPositiveButton(R.string.upgrade, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   //OtaUtil.startUpgrade(mContext,file,R.string.local_file_can_upgrade, R.string.cancel);
                    OtaUtil.updateOta(mContext,file);
                }
            }).setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    file.delete();
                    Toast.makeText(BootCheckUpdateActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        }
    }
    protected void showProgressDialog(int resid) {
        mDialog = ProgressDialog.show(mContext, null, getString(resid), true, true);
        mDialog.setCancelable(false);
        mDialog.show();

    }

    protected void dismissProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


}
