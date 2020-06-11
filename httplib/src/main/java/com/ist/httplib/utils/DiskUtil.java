package com.ist.httplib.utils;

import android.content.Context;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.ist.httplib.OtaLibConfig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengshaorui
 * Time on 2018/9/15
 */

public class DiskUtil {
    private static final String TAG = "DiskUtil";
    public static List<String> getUSBInfo(){
        List<String> paths = new ArrayList<>();
        StorageManager storageManager = (StorageManager) OtaLibConfig.getBuilder().getContext().getSystemService(Context.STORAGE_SERVICE);
            try {
            Class<?> storageClass = Class.forName("android.os.storage.StorageManager");
            Method method = storageClass.getDeclaredMethod("getVolumeList");
            method.setAccessible(true);
            StorageVolume[] volumes = (StorageVolume[]) method.invoke(storageManager);
            for (StorageVolume volume : volumes) {
                Class<?> volumeClass = Class.forName("android.os.storage.StorageVolume");
                Method getpath = volumeClass.getDeclaredMethod("getPath");
                String path = (String) getpath.invoke(volume);
                Log.e("Keven", "getUSBInfo: path =="+path);
                if ("/storage/emulated/0".equals(path)){
                    continue;
                }
                if (path.contains("/mnt/usb")) {
                    paths.add(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return paths;
    }

}
