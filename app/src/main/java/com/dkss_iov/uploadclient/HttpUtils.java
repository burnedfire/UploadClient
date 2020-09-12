package com.dkss_iov.uploadclient;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import okhttp3.*;

/**
 *
 */
public class HttpUtils {

    private static final  UUID uuid= UUID.randomUUID();
    private static final String TAG = "Utils";
    private static final String dirPath = "/mnt/sdcard/FtpFilePath";
    private static List<String> allFileNames = null;
    private static OkHttpClient okHttpClient = null;
    private static boolean  flag = false;
    //上传file code type
    public static Response upload(String url,File file,String code,String type) throws IOException {

        // postman
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("multipart/form-data");
        Log.d(TAG, "upload: " + file.getName());
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("device-file[]", file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                file))
                .addFormDataPart("code", code)
                .addFormDataPart("type", type)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "multipart/form-data")
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.d(TAG, "upload:  网络异常");
            //e.printStackTrace();
        }
        return response;
    }
    //解析返回的response
    public  static String parseJsonWithJsonObject(Response response) throws IOException {
        String responseData = response.body().string();
        try{
            JSONObject jsonObject = new JSONObject(responseData);
            String message = jsonObject.getString("code");
           // String message = jsonObject.getString("code");
            Log.d(TAG, "parseJsonWithJsonObject: message = " + message);
            return message;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    //获取目录下所有文件
    public static List<File> getFilesAllName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();
        if (files == null){
            Log.e("error","空目录");
            return null;
        }
        List<File> fileList = new ArrayList<File>(Arrays.asList(files));

        return fileList;
    }

}
