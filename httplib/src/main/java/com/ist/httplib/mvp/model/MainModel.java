package com.ist.httplib.mvp.model;

import android.content.Context;
import android.util.Log;

import com.ist.httplib.OtaLibConfig;
import com.ist.httplib.mvp.contract.OtaContract;
import com.ist.httplib.status.UsbCheckStatus;
import com.ist.httplib.utils.DiskUtil;
import com.ist.httplib.utils.SprefUtils;

import java.io.File;
import java.util.List;

/**
 * Created by zhengshaorui
 * Time on 2018/9/26
 */

public class MainModel {

    private OtaLibConfig.Builder mBuilder;
    private Context mContext;
    private OtaContract.IMainView mIView;
    public static MainModel create(OtaContract.IMainView iView){
        return new MainModel(iView);
    }
    private MainModel(OtaContract.IMainView iView){
        mBuilder = OtaLibConfig.getBuilder();
        mContext = mBuilder.getContext();
        mIView = iView;
        checkLocalFileExsits();
    }

    private void checkLocalFileExsits() {
        final File file = new File(mBuilder.getFilePath(),mBuilder.getFileName());
        boolean isRemindMe = (boolean) SprefUtils.getSprefValue(SprefUtils.KEY_REMIND_ME, SprefUtils.SprefType.BOOLEAN);
        mIView.isLocalHasFile(file.exists() && isRemindMe,file);
    }

    /**
     * 本地升级
     */
    public void checkLocalUpdate() {
        List<String> paths = DiskUtil.getUSBInfo();
        if (!paths.isEmpty()){
            if (paths.size() > 1){
                mIView.checkUsbFile(UsbCheckStatus.MORE_THAN_ONE,null);
                return;
            }
            String path = paths.get(0);
            Log.e("Keven", "checkLocalUpdate: path =="+path);
            File file = new File(path);
            if (file.exists()){
                File[] files = file.listFiles();
                for (File dir : files) {
                    Log.e("Keven", "checkLocalUpdate: mBuilder.getFileName =="+mBuilder.getFileName());
                    Log.e("Keven", "checkLocalUpdate: dir.getName =="+dir.getName());
                    if (mBuilder.getFileName().equals(dir.getName())){
                        mIView.checkUsbFile(UsbCheckStatus.SUCCESS,dir);
                        return;
                    }
                }
                mIView.checkUsbFile(UsbCheckStatus.NO_UPGRADE_FILE,null);
                return;
            }
        }
        mIView.checkUsbFile(UsbCheckStatus.NO_USB,null);
    }

}
