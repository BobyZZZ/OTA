package com.ist.otaservice.fragment;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ist.httplib.InvokeManager;
import com.ist.httplib.OtaLibConfig;
import com.ist.httplib.bean.BaseOtaRespone;
import com.ist.httplib.mvp.contract.OtaContract;
import com.ist.httplib.mvp.present.MainPresent;
import com.ist.httplib.net.NetErrorMsg;
import com.ist.httplib.status.UsbCheckStatus;
import com.ist.otaservice.BuildConfig;
import com.ist.otaservice.Constant;
import com.ist.otaservice.CustomerConfig;
import com.ist.otaservice.MainApplication;
import com.ist.otaservice.R;
import com.ist.otaservice.utils.CusFragmentManager;
import com.ist.otaservice.utils.IstUtil;
import com.ist.otaservice.utils.OtaUtil;

import java.io.File;

/**
 * Created by zhengshaorui
 * Time on 2018/9/15
 */

public class MainFragment extends BaseFragment<MainPresent> implements View.OnClickListener,
        OtaContract.IMainView{
    private static final String TAG = "MainFragment";
    private TextView mTextView;
    private MainPresent mPresent;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public MainPresent getPresent() {
        mPresent = MainPresent.create(this);
        return mPresent;
    }

    @Override
    public void initView(View view) {

        if ("on".equals(InvokeManager.get("ist.ota.debug","off"))){
            CustomerConfig.ISDEBUG = true;
            OtaLibConfig.getBuilder().setDebug(true);
        }

        view.findViewById(R.id.btn_net).setOnClickListener(this);
        view.findViewById(R.id.btn_local).setOnClickListener(this);
        mTextView = view.findViewById(R.id.main_current_tv);

        String version = InvokeManager.get("ro.product.version","v1.0.0");
        mTextView.setText(getString(R.string.current_vertion,version));


        //release 版本不显示本地升级
        if (!BuildConfig.DEBUG) {
            view.findViewById(R.id.btn_local).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onPressBack() {
        return false;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_net) {
            Log.e(TAG, "Constant.SAVE_PATH =="+ Constant.SAVE_PATH);
            if (IstUtil.isNetworkPositive()) {
                showProgressDialog(R.string.check);
                MainApplication.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPresent.checkNetUpdate();
                    }
                }, 1500);
            } else {
                Toast.makeText(mContext, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }
        }

        if (v.getId() == R.id.btn_local) {
            mPresent.checkLocalUpdate();

        }
    }




    @Override
    public void error(NetErrorMsg status, String errorMsg) {
        Log.d(TAG, "zsr --> mainfragment: "+status+" "+errorMsg);
        dismissProgressDialog();
        switch(status){
            case SERVER_NOT_FOUND:
                Log.e("Keven", "error: SERVER_NOT_FOUND");
                Toast.makeText(mContext,R.string.others_error, Toast.LENGTH_SHORT).show();
                 break;
            case ID_NOT_SAME:
                Log.e("Keven", "error: ID_NOT_SAME");
                Toast.makeText(mContext,R.string.id_not_same, Toast.LENGTH_SHORT).show();
                break;
            case RULES_NOT_SAME :
                Log.e("Keven", "error: RULES_NOT_SAME");
                Toast.makeText(mContext,R.string.server_not_found, Toast.LENGTH_SHORT).show();
                break;
            case OTHERS:
                Log.e("Keven", "error: OTHERS");
                Toast.makeText(mContext,R.string.server_not_found, Toast.LENGTH_SHORT).show();
                break;
            default :
                Log.e("zyc", "error: OTHERS 2");
                Toast.makeText(mContext,R.string.others_error, Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public void isLocalHasFile(boolean isLocalhasFile,final File file) {
        if (isLocalhasFile) {
            //请在这里做校验升级

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(R.string.local_has_file)
                    .setPositiveButton(R.string.verify, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isAdded()) {
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
                                        OtaUtil.startUpgrade(mContext, file, R.string.local_file_can_upgrade, R.string.delete);
                                    }

                                    @Override
                                    public void fail(String errorMsg) {
                                        dismissProgressDialog();
                                        Toast.makeText(mContext, getString(R.string.check_your_file), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).setNegativeButton(R.string.cancel, null);
            builder.create().show();
        }

    }

    @Override
    public void checkUsbFile(UsbCheckStatus usbCheckStatus,final File file) {
        switch(usbCheckStatus){
            case SUCCESS:
                if (file != null && getActivity() != null){
                    showProgressDialog(R.string.check);
                    OtaUtil.checkLocalHasUpdateFile(file, new OtaUtil.CheckListener() {
                        @Override
                        public void check(int progress) {
                            if (mDialog != null && mDialog.isShowing()){
                                mDialog.setMessage(getString(R.string.check)+"     -->    "+progress+"%");
                            }
                        }

                        @Override
                        public void success() {
                            dismissProgressDialog();
                            OtaUtil.startUpgrade(mContext,file,R.string.local_file_can_upgrade, R.string.cancel);
                        }

                        @Override
                        public void fail(String errorMsg) {
                            dismissProgressDialog();
                            Toast.makeText(mContext, getString(R.string.check_your_file), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    dismissProgressDialog();
                }
                 break;
            case MORE_THAN_ONE :
                Toast.makeText(mContext, getString(R.string.more_usb), Toast.LENGTH_SHORT).show();
                break;
            case NO_UPGRADE_FILE :
                Toast.makeText(mContext, getString(R.string.no_updatefile_usb), Toast.LENGTH_SHORT).show();
                break;
            case NO_USB :
                Toast.makeText(mContext, getString(R.string.no_usb_here), Toast.LENGTH_SHORT).show();
                break;
            default :
                break;

        }
    }

    @Override
    public void checkNetUpdate(boolean isCanUpdate, BaseOtaRespone respone) {
        dismissProgressDialog();
        if (isCanUpdate) {
           // String version = InvokeManager.get("ro.product.version","V1.0.0.0");
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getString(R.string.check_new_version))
                    .setNegativeButton(R.string.load_back,  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CusFragmentManager.getInstance().replaceFragment(DownloadFragment.newInstance("on"), CusFragmentManager.RIGHT);

                        }
                    }).setPositiveButton(R.string.cancel, null);
            builder.create().show();
        } else {
            Toast.makeText(mContext, getString(R.string.no_new_fireware), Toast.LENGTH_SHORT).show();
        }
    }
}
