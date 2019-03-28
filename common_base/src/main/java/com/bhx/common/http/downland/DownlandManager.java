package com.bhx.common.http.downland;

import com.bhx.common.http.RetrofitManager;
import com.bhx.common.http.upload.UploadManager;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 下载得管理类
 */
public class DownlandManager {

    public static void downland(String url, FileInfo fileInfo) {
        ApiService apiService = RetrofitManager.getInstance().createApiService(ApiService.class);
        
        apiService.downland(url);

    }


    interface ApiService {
        @GET
        @Streaming
        Observable<ResponseBody> downland(@Url String url);
    }
}
