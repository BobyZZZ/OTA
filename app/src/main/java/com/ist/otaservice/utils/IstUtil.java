package com.ist.otaservice.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.ist.otaservice.MainApplication;

/**
 * Created by zhengshaorui
 * Time on 2018/9/13
 */

public class IstUtil {
    private static final String TAG = "IstUtil";
    private static Context sContext = MainApplication.CONTEXT;



    /**
     * 判断是否有网络
     * @return
     */
    public static boolean isNetworkPositive() {
        ConnectivityManager connectivityManager = (ConnectivityManager)sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWIFIC = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        boolean isETHERNETC = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).isConnected();
        if (isWIFIC || isETHERNETC){
            return true;
        }
        return false;
    }




    public static  void showBarView(View mainview,View barview, final boolean showbar, int time){
        ObjectAnimator mainAnimator;
        ObjectAnimator barAnimator;
        if (showbar){
            //mBarLy.setVisibility(View.VISIBLE);
            mainAnimator = ObjectAnimator.ofFloat(mainview,"translationX",0,1280);
            barAnimator = ObjectAnimator.ofFloat(barview,"alpha",0,1);

        }else{
            //  mMainLy.setVisibility(View.VISIBLE);
            barAnimator = ObjectAnimator.ofFloat(barview,"alpha",1,0);
            mainAnimator = ObjectAnimator.ofFloat(mainview,"translationX",1280,0);
        }

        mainAnimator.setInterpolator(new LinearInterpolator());
        mainAnimator.setDuration(time);
        mainAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);


            }
        });
        mainAnimator.start();

        barAnimator.setInterpolator(new LinearInterpolator());
        barAnimator.setDuration(time);
        barAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
        barAnimator.start();
    }


    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) <= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }



}
