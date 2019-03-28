package com.bhx.common;

import com.bhx.common.http.RetrofitManager;
import com.bhx.common.http.upload.UploadManager;
import com.bhx.common.http.upload.UploadParam;
import com.bhx.common.http.upload.UploadProgressObserver;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;

public class Test {

    public static void main(String args[]) {
        RetrofitManager.getInstance().init();

        UploadParam params = new UploadParam("file", new File("C:\\Users\\DELL\\Desktop\\dimens.xml"));
        UploadManager.uploadFile("http://localhost:8080/Web_Test/servlet/UploadServlet", params,
                new UploadProgressObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody body) {
                        try {
                            System.out.println("上传成功:" + body.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                        System.out.println("上传失败:" + throwable.getLocalizedMessage());
                    }

                    @Override
                    public void onProgress(UploadParam params, int percent) {
                        System.out.println("上传进度：" + percent);
                    }
                });
    }

}
