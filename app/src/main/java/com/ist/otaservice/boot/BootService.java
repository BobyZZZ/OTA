package com.ist.otaservice.boot;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.UserManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.ist.httplib.LitepalManager;
import com.ist.httplib.bean.BaseOtaRespone;
import com.ist.httplib.net.HttpTaskManager;
import com.ist.httplib.net.NetErrorMsg;
import com.ist.httplib.net.callback.UpdateListenerAdapter;
import com.ist.otaservice.Constant;
import com.ist.otaservice.CustomerConfig;
import com.ist.otaservice.MainActivity;
import com.ist.otaservice.MainApplication;
import com.ist.otaservice.R;
import com.ist.otaservice.utils.IstUtil;
import com.ist.otaservice.utils.OtaUtil;
import com.ist.otaservice.utils.SprefUtils;

import java.io.File;

/**
 * @author zhengshaorui
 * @date 2018/10/17
 * 该 service 用于开机第一次启动
 */
public class BootService extends Service {
    private static final String TAG = "BootService";
    private static final String CHANNEL_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String WIFI_CHANGE = "android.net.wifi.STATE_CHANGE";
    private Context mContext;
    private boolean isFirstLauncher = true ;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: [BootService]");
        mContext = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_BACKGROUND_NET_UPDATE);
        filter.addAction(CHANNEL_CHANGE);
        filter.addAction(WIFI_CHANGE);
        registerReceiver(LocalReceiver,filter);
        if (CustomerConfig.USE_DB) {
            LitepalManager.getInstance().deleteall();
        }

    }


    BroadcastReceiver LocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("Keven", "onReceive: action =="+action);
            if (Constant.ACTION_BACKGROUND_NET_UPDATE.equals(action)) {
                Intent bootactivity = new Intent(context, BootCheckUpdateActivity.class);
                bootactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                bootactivity.putExtra("status", "net");
                context.startActivity(bootactivity);
            }else{
               if (IstUtil.isNetworkPositive() && isFirstLauncher){

                    isFirstLauncher = false;
                    final File file = new File(Constant.SAVE_PATH+File.separator+ Constant.FILE_NAME);
                    if (!file.exists()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (isAnim()) {
                                checkNetUpdate();
                            }
                        }else{
                            checkNetUpdate();
                        }
                    }

               }
            }



        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (isAnim()) {
                checkLocalFile();
            }
        }else{
            checkLocalFile();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void checkLocalFile() {
        final File file = new File(Constant.SAVE_PATH+File.separator+ Constant.FILE_NAME);
        if (file.exists()) {
            OtaUtil.checkLocalHasUpdateFile(file, new OtaUtil.CheckListener() {
                @Override
                public void check(int progress) {
                    Log.d(TAG, "zsr --> check: "+progress);
                }

                @Override
                public void success() {
                    //2s 之后再提示是否升级
                    MainApplication.HANDLER.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent bootactivity = new Intent(mContext, BootCheckUpdateActivity.class);
                            bootactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(bootactivity);
                        }
                    },2000);
                }

                @Override
                public void fail(String errorMsg) {
                    Log.d(TAG, "zsr --> fail: "+errorMsg);
                    if (CustomerConfig.USE_DB) {
                        LitepalManager.getInstance().deleteall();
                    }
                    SprefUtils.saveSprefValue(SprefUtils.KEY_REMIND_ME,false);
                    file.delete();
                    checkNetUpdate();
                }
            });
        }
    }



    private void checkNetUpdate() {

        HttpTaskManager.getInstance().checkUpdate(new UpdateListenerAdapter() {
            @Override
            public void checkUpdate(boolean isCanUpdate, BaseOtaRespone respone) {
                super.checkUpdate(isCanUpdate,respone);
                if (isCanUpdate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setCancelable(false)
                            .setMessage(getString(R.string.check_new_version))
                            .setPositiveButton(R.string.load_back, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    File file = new File(Constant.SAVE_PATH,Constant.FILE_NAME);
                                    if (file.exists()){
                                        file.delete();
                                    }
                                    if (!isAnim()){
                                        Toast.makeText(mContext, R.string.not_anim, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("service", true);
                                    mContext.startActivity(intent);
                                }
                            }).setNegativeButton(R.string.cancel, null);

                    Dialog dialog = builder.create();
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

                    dialog.show();
                }

            }

            @Override
            public void error(NetErrorMsg status, String errorMsg) {
                super.error(status, errorMsg);
                Log.d(TAG, "zsr --> error: " + status + " " + errorMsg);
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(LocalReceiver);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private boolean isAnim(){
       UserManager userManager = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
        /* try {
            Method method = userManager.getClass().getDeclaredMethod("isAdminUser");

            method.setAccessible(true);
            boolean isuser  = (boolean) method.invoke(userManager);
            Log.d(TAG, "zsr --> isAnim: "+isuser+ " "+userManager.isSystemUser());
            return isuser;
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return userManager.isSystemUser();
    }
}
