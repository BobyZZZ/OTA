package com.ist.httplib.net.retrofit;


import com.ist.httplib.bean.BaseOtaRespone;
import com.ist.httplib.bean.customer.AppVersion;
import com.ist.httplib.bean.customer.KangjiaBean;
import com.ist.httplib.bean.customer.SkyworthBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by zhengshaorui
 * Time on 2018/8/14
 * retrofit 不能穿泛型，所以这用国际通用类型 String
 */

public interface HttpServer {



    @Streaming
    @GET
    Observable<ResponseBody> getFileLength(@Url String url);

    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url, @Header("RANGE") String range);

    @Streaming
    @GET
    Observable<Response<ResponseBody>> download(@Url String url);



    /**
     * 返回不同的update.json
     */
    @GET
    Observable<BaseOtaRespone> get(@Url String url);


    @GET
    Observable<BaseOtaRespone<AppVersion>>  getI3Json(@Url String url);

}
