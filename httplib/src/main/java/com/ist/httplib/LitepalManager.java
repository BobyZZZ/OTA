package com.ist.httplib;


import com.ist.httplib.bean.ThreadBean;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by zhengshaorui
 * Time on 2018/9/12
 */

public class LitepalManager {
    private static class Holder {
        static final LitepalManager INSTANCE = new LitepalManager();
    }

    public static LitepalManager getInstance() {
        return Holder.INSTANCE;
    }

    private LitepalManager() {
    }

    public void saveOrUpdate(ThreadBean bean){
        bean.saveOrUpdate("theadId = ?",bean.theadId+"");
    }

    public List<ThreadBean> getAllThreadBean(){
        return LitePal.findAll(ThreadBean.class,true);
    }

    public void deleteall(){
        List<ThreadBean> list = getAllThreadBean();
        for (ThreadBean threadBean : list) {
            threadBean.delete();
        }
    }

    public boolean isDbExsits(){
        return LitePal.isExist(ThreadBean.class);
    }
}
