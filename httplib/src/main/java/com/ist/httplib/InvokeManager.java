package com.ist.httplib;


import android.graphics.Bitmap;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by zhengshaorui
 * Time on 2018/9/11
 */

public class InvokeManager {
    private static final String TAG = "InvokeManager";
    //为了方便混淆，用反射的方式

    public static void  set(String key,String value){
        try {
            Class<?> pClass = Class.forName("android.os.SystemProperties");
            Method method = pClass.getMethod("set",String.class,String.class);
            method.setAccessible(true);
            method.invoke(pClass,key,value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key,String defaultValue){
        try {
            Class<?> pClass = Class.forName("android.os.SystemProperties");
            Method method = pClass.getMethod("get",String.class,String.class);
            method.setAccessible(true);
            return (String) method.invoke(pClass,key,defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static Bitmap screenshot(int widht, int height){
        Bitmap bitmap = null;
        try {
            Class<?> sClass = Class.forName("android.view.SurfaceControl");
            Method method = sClass.getMethod("screenshot",int.class,int.class);
            method.setAccessible(true);
            bitmap = (Bitmap) method.invoke(sClass,widht,height);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "zsr --> screenshot: "+e.toString());
        }
        return bitmap;
    }
}
