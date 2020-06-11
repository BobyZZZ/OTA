package com.ist.otaservice.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.ist.otaservice.MainApplication;

import java.util.Set;

/**
 * Created by zhengshaorui on 2017/2/24.
 */

public class SprefUtils {
    private static final String FILE_NAME = "otaspref";
    public static final String KEY_IS_DOWNLOAD = "isdownload"; //表示有下载任务，下次得从断点开始下载
    public static final String KEY_REMIND_ME = "remind_me"; //文件已经下载，下次启动提示是否升级
    public static final String KEY_ISPAUSE = "ispause"; //文件已经下载，下次启动提示是否升级

    private static Context sContext = MainApplication.CONTEXT;


    public static enum SprefType{
        FLOAT,
        INT,
        LONG,
        BOOLEAN,
        STRING,
        SET,
    }
    /**
     * 保存数据
     * @param key
     * @param value
     */
    public static void saveSprefValue(String key,Object value){
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor =
                sContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit();
        if (value instanceof  Float){
            editor.putFloat(key, (Float) value);
        }else if(value instanceof Integer){
            editor.putInt(key, (Integer) value);
        }else if(value instanceof Long){
            editor.putLong(key, (Long) value);
        }else if(value instanceof  Boolean){
            editor.putBoolean(key, (Boolean) value);
        }
        else if(value instanceof Set){
            editor.putStringSet(key, (Set<String>) value);
        }else{
            editor.putString(key, (String) value);
        }
        editor.commit();
    }

   

    public static Object getSprefValue(String key,SprefType type){
        SharedPreferences preferences = sContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        if (type == SprefType.FLOAT){
            return preferences.getFloat(key,-1);
        }else if (type == SprefType.INT){
            return preferences.getInt(key,-1);
        }
        else if (type == SprefType.LONG){
            return preferences.getLong(key,-1);
        }else if (type == SprefType.BOOLEAN){
            return preferences.getBoolean(key,false);
        }else if (type == SprefType.SET){
            return preferences.getStringSet(key,null);
        }else if (type == SprefType.STRING){
            return preferences.getString(key,null);
        }else {
            return preferences.getAll();
        }

    }

    public static void clearAll(){
        SharedPreferences.Editor editor =
                sContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }


}