package com.ist.otaservice.utils;

import android.annotation.SuppressLint;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.ist.otaservice.R;

import java.util.List;

/**
 * Created by zhengshaorui
 * Time on 2018/8/7
 */
public class CusFragmentManager {
    private static final String TAG = "CusFragmentManager";
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private FragmentManager mFragmentManager;
    private int mContentId;
    private TextView mTextView;

    private CusFragmentManager(){

    }
    private static class Holder{
        static CusFragmentManager INSTANCE = new CusFragmentManager();
    }

    public static CusFragmentManager getInstance(){
        return Holder.INSTANCE;
    }

    public CusFragmentManager config(FragmentManager fm, @IdRes int contentId){
        mFragmentManager = fm;
        mContentId = contentId;
        return this;
    }

    public CusFragmentManager config(TextView textView){
        mTextView = textView;
        return this;
    }

    public CusFragmentManager setTitle(String msg){
        if (mTextView != null){
            mTextView.setText(msg);
        }
        return this;
    }

    /**
     * 加载第一个fragment
     * @param fragment
     */
    public CusFragmentManager loadRootFragment(Fragment fragment){
        addOrShowFragment(fragment);
        return this;
    }

    /**
     * 加载第一个fragment
     * @param fragment
     * @param tag
     */
    public void loadRootFragment(Fragment fragment,String tag){
        mFragmentManager.beginTransaction().add(mContentId,fragment,tag).commit();
    }


    /**
     * 隐藏所有的fragment
     */
    public void hideAllFragment(){
        List<Fragment> fragments = mFragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null){
                FragmentTransaction ft =  mFragmentManager.beginTransaction();
                ft.hide(fragment);
                ft.commit();
            }

        }

    }

    /**
     * 添加或者显示fragment
     * @param fragment
     */
    public void addOrShowFragment(Fragment fragment){
        if (fragment != null){
            FragmentTransaction ft =  mFragmentManager.beginTransaction();
            if (!fragment.isAdded()){
                ft.add(mContentId,fragment,fragment.getClass().getName());
            }
            ft.show(fragment);
            ft.commit();
        }


    }

    /**
     * 添加或者显示fragment
     * @param fragment
     * @param tag
     */
    public  void addOrShowFragment(Fragment fragment,String tag){
        if (fragment != null){
            FragmentTransaction ft =  mFragmentManager.beginTransaction();
            if (!fragment.isAdded()){
                ft.add(mContentId,fragment,tag);
            }
            ft.show(fragment);
            ft.commit();
        }

    }

    /**
     * 添加或者显示fragment
     * @param fragment
     * @param direction
     */
    public  void addOrShowFragment(Fragment fragment,int  direction){
        if (fragment != null){
            if (direction == 0){

                FragmentTransaction ft =  mFragmentManager.beginTransaction();
                if (!fragment.isAdded()){
                    ft.add(mContentId,fragment);
                }
                ft.show(fragment)
                        .setCustomAnimations(R.anim.left_in,R.anim.right_out);
                ft.commit();
                mFragmentManager.beginTransaction()

                        // .addToBackStack(null)
                        //前面两个为进入和退出，后面则为进栈和出栈
                        .replace(mContentId, fragment).commit();
            }else{

                FragmentTransaction ft =  mFragmentManager.beginTransaction();
                if (!fragment.isAdded()){
                    ft.add(mContentId,fragment);
                }
                ft.show(fragment)
                        .setCustomAnimations(R.anim.right_in,R.anim.left_out);
                ft.commit();
                mFragmentManager.beginTransaction()

                        // .addToBackStack(null)
                        //前面两个为进入和退出，后面则为进栈和出栈
                        .replace(mContentId, fragment).commit();
            }

        }

    }

    /**
     * 移除fragment
     * @param fragment
     */
    public void removeFragment(Fragment fragment){
        if (fragment != null) {
            mFragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

    /**
     * 替换fragment
     * @param fragment
     */
    @SuppressLint("ResourceType")
    public void replaceFragment(Fragment fragment){
        if (fragment != null) {
            mFragmentManager.beginTransaction()
                   // .addToBackStack(null)
                    //前面两个为进入和退出，后面则为进栈和出栈
                    .replace(mContentId, fragment).commit();
        }
    }

    public void replaceFragment(Fragment fragment,int direction){
        if (fragment != null) {
            if (direction == 0){
                mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.left_in,R.anim.right_out)
                        // .addToBackStack(null)
                        //前面两个为进入和退出，后面则为进栈和出栈
                        .replace(mContentId, fragment,fragment.getClass().getName()).commit();
            }else{

                mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.right_in,R.anim.left_out)
                        // .addToBackStack(null)
                        //前面两个为进入和退出，后面则为进栈和出栈
                        .replace(mContentId, fragment,fragment.getClass().getName()).commit();
            }

        }

    }

    /**
     * 替换fragment
     * @param fragment
     * @param tag
     */
    @SuppressLint("ResourceType")
    public void replaceFragment(Fragment fragment, String tag){
        if (fragment != null) {
            mFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.right_in,R.anim.left_out,
                            R.anim.left_in,R.anim.right_out)
                    //.addToBackStack(null)

                    .replace(mContentId, fragment,tag).commit();
        }
    }

    /**
     * 获取当前fragment
     * @return
     */
    public Fragment getCurrentFragment(){
        List<Fragment> fragments = mFragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()){
                return fragment;
            }
        }
        return null;
    }


}