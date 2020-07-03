package com.ist.otaservice.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ist.httplib.InvokeManager;
import com.ist.httplib.mvp.contract.OtaContract;
import com.ist.httplib.mvp.present.DownloadPresent;
import com.ist.httplib.net.HttpTaskManager;
import com.ist.httplib.net.NetErrorMsg;
import com.ist.httplib.utils.RxUtils;
import com.ist.httplib.utils.SprefUtils;
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

public class DownloadFragment extends BaseFragment<DownloadPresent> implements OtaContract.IDownloadView, View.OnClickListener {
    private static final String TAG = "DownloadFragment";
    /**
     * view
     */
    private ProgressBar mProgressBar;
    private TextView mProgressTv, mDownloadSpeedTv, mDownloadSizeTv;
    private TextView mDeviceNameTv, mDeviceVertionTv, mDownloadStatusTv;
    private Button mPauseBtn, mRestartBtn;

    /**
     * logic
     */

    private long mLastLength = 0;
    private DownloadPresent mPresent;

    public static DownloadFragment newInstance(String status) {
        DownloadFragment fragment = new DownloadFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.START_DOWNLOAD, status);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_download;
    }

    @Override
    public DownloadPresent getPresent() {
        mPresent = DownloadPresent.create(this);
        return mPresent;
    }

    @Override
    public void initView(View view) {
        mProgressBar = view.findViewById(R.id.progress);
        mProgressTv = view.findViewById(R.id.progress_tv);
        mDownloadSpeedTv = view.findViewById(R.id.download_speed_tv);
        mDownloadSizeTv = view.findViewById(R.id.download_size_tv);
        mPauseBtn = view.findViewById(R.id.btn_puase);
        mDeviceNameTv = view.findViewById(R.id.download_device_name);
        mDeviceVertionTv = view.findViewById(R.id.download_device_version);
        mDownloadStatusTv = view.findViewById(R.id.download_status);
        mRestartBtn = view.findViewById(R.id.btn_redownload);
        mPauseBtn.setOnClickListener(this);
        mRestartBtn.setOnClickListener(this);
        initData();
    }

    private void initData() {

        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.d(TAG, "initData Constant.START_DOWNLOAD: " + bundle.getString(Constant.START_DOWNLOAD, "off"));
            if ("on".equals(bundle.getString(Constant.START_DOWNLOAD, "off"))) {
                mPresent.startDownload();
                mDownloadStatusTv.setText(R.string.downloading);
            } else if ("restart".equals(bundle.getString(Constant.START_DOWNLOAD, "off"))) {
                //先暂停再重新下载
                if (CustomerConfig.USE_DB) {
                    HttpTaskManager.getInstance().pause();// mPresent.pauseDownload
                    MainApplication.HANDLER.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPresent.startDownload();
                        }
                    }, 500);
                    mDownloadStatusTv.setText(R.string.downloading);
                } else {
                    mPresent.startDownload();
                    mDownloadStatusTv.setText(R.string.downloading);
                }

            }
        }
        mDeviceNameTv.setText(InvokeManager.get("ro.product.name", "Model"));
        mDeviceVertionTv.setText(InvokeManager.get("ro.product.version", "v1.0.0"));

    }

    @Override
    public void error(NetErrorMsg status, String errorMsg) {
        Log.d(TAG, "zsr --> error: " + status + " " + errorMsg);
        Log.e("Keven", "error: Constant.SAVE_PATH ==" + Constant.SAVE_PATH);
        File file = new File(Constant.SAVE_PATH, Constant.FILE_NAME);
        mPauseBtn.setText(R.string.start);
        switch (status) {
            case CACHE_NOT_ENOUGH:
                if (file.exists()) {
                    showDeleteDialog(file);
                }
                break;
            case RESPONSE_IS_NULL:
                mDownloadStatusTv.setText(R.string.file_damaged);
                mPauseBtn.setText(R.string.try_again);
                break;
            case TIME_OUT:
                mDownloadStatusTv.setText(R.string.time_out);
                mPauseBtn.setText(R.string.try_again);
                break;
            case FILE_LENGTH_NOT_SAME:

                if (file.exists()) {
                    if (isAdded()) {
                        showDeleteDialog(file);
                    }
                }
                break;
            case SERVER_NOT_FOUND:
            case OTHERS:
                mDownloadStatusTv.setText(R.string.others_error);
                mPauseBtn.setText(R.string.try_again);
                break;
            case DATABASE_FIELD_NOT_MATCH:
                mDownloadStatusTv.setText(R.string.db_field_not_match);
                mPauseBtn.setText(R.string.try_again);
                break;
            default:
                break;

        }
    }

    @Override
    public void updateProgress(int progress) {
        //Log.d(TAG, "zsr --> updateProgress: "+progress);
        if (isAdded()) {
            mProgressBar.setProgress(progress);
            mProgressTv.setText(progress + "%");
        }

    }

    @Override
    public void updateOtherInfo(long currentSize, long totalSize) {
        Log.d(TAG, "zsr --> updateProgress: " + Formatter.formatFileSize(mContext, (currentSize - mLastLength)) + "---mPresent.isRunning(): " + mPresent.isRunning());
        if (isAdded() && mPresent.isRunning()) {
            long size = (currentSize - mLastLength);
            String speed = Formatter.formatFileSize(mContext, size) + "/s";
            String current = Formatter.formatFileSize(mContext, (currentSize));
            String total = Formatter.formatFileSize(mContext, (totalSize));
            if (size > 0 && size > 100) {
                mDownloadSpeedTv.setText(speed);
            }
            mDownloadSizeTv.setText(getString(R.string.download_size, current, total));
            mDownloadStatusTv.setText(R.string.downloading);
            mPauseBtn.setText(R.string.pause);
            mLastLength = currentSize;
        }
    }

    @Override
    public void downloadSuccess() {
        if (isAdded()) {
            mProgressBar.setProgress(100);
            mProgressTv.setText(100 + "%");
            mDownloadSizeTv.setText("");
            mDownloadSpeedTv.setText("");
            mDownloadStatusTv.setText(R.string.download_success);
            mPauseBtn.setVisibility(View.GONE);
            mRestartBtn.setVisibility(View.GONE);
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
                        Log.d(TAG, "zsr --> success: " + file.getAbsolutePath() + " " + file.getName());
                        OtaUtil.startUpgrade(mContext, file, R.string.local_file_can_upgrade, R.string.remind_me);
                    }

                    @Override
                    public void fail(String errorMsg) {
                        MainApplication.HANDLER.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mDownloadStatusTv.setText(getString(R.string.verification_failed));
                                dismissProgressDialog();
                                Toast.makeText(mContext, getString(R.string.verification_failed), Toast.LENGTH_SHORT).show();
                            }
                        }, 500);
                        Log.d(TAG, "zyc-> fail currentThread: " + Thread.currentThread().getName());
                    }
                });
            } else {
                dismissProgressDialog();
            }
        } else {
            //这个时候后台下载成功了，但是没有界面，所以，我们需要通过弹出一个
            //activity，让它去提示下载成功
            mContext.sendBroadcast(new Intent(Constant.ACTION_BACKGROUND_NET_UPDATE));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresent.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (!IstUtil.isNetworkPositive()) {
            Toast.makeText(mActivity, R.string.no_network, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (v.getId()) {
            case R.id.btn_puase:
                if (!IstUtil.isFastClick()) {
                    if (!mPresent.isPause()) {
                        mPauseBtn.setText(R.string.start);
                        mDownloadStatusTv.setText(R.string.download_pause);
                        mPresent.pauseDownload();
                    } else {
                        mPauseBtn.setText(R.string.pause);
                        mDownloadStatusTv.setText(R.string.downloading);
                        mPresent.startDownload();
                    }
                } else {
                    Toast.makeText(mActivity, R.string.click_too_fast, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_redownload:
                mPresent.reDownload();
                break;
            default:
                break;

        }
    }

    private void showDeleteDialog(final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.cache_not_enough)
                .setPositiveButton(R.string.delete_redownload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RxUtils.deleteDbFile();
                        mPresent.startDownload();
                    }
                }).setNegativeButton(R.string.cancel, null).create().show();


    }

    @Override
    public boolean onPressBack() {
        boolean remindMe = (boolean) SprefUtils.getSprefValue(SprefUtils.KEY_REMIND_ME, SprefUtils.SprefType.BOOLEAN);
        if (remindMe) {
            //校验成功，弹出过对话框并选择了remindMe，则直接返回，不执行下边的删除操作
            CusFragmentManager.getInstance().replaceFragment(MainFragment.newInstance(), CusFragmentManager.LEFT);
            return true;
        }
        new AlertDialog.Builder(mActivity)
                .setMessage(R.string.back_download_tip)
                .setNegativeButton(R.string.cancel_download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresent.pauseDownload();
                        if (CustomerConfig.USE_DB) {
/*                            MainApplication.HANDLER.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    RxUtils.deleteDbFile();
                                }
                            }, 500);*/
                            Log.d(TAG, "onPressBack currentThread: " + Thread.currentThread().getName());
                            //直接在当前线程删除文件，防止这里先调用删除文件，回到MainFragment仍然能检测到文件问题
                            RxUtils.deleteDbFileInCurrentThread();
                        }
                        CusFragmentManager.getInstance().replaceFragment(MainFragment.newInstance(), CusFragmentManager.LEFT);
                    }
                })
                .setPositiveButton(R.string.back_download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.moveTaskToBack(true);
                    }
                }).create().show();
        return true;
    }
}
