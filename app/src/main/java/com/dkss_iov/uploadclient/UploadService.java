package com.dkss_iov.uploadclient;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Response;

public class UploadService extends Service {
    private static final String TAG = "UploadService";
    private String CODE = "code-E65";
    private  String TYPE = "file";
    private  String dirPath = "/mnt/sdcard/FtpFilePath/";
    private  File file2upload = null;
   // private  String url = "http://172.20.10.4:8080/uploadImg";
    //private  String url = "http://192.168.1.10:8080/uploadImg";
    //private  String url = "http://47.113.81.214:8084/api/upload";
    private  String url = "http://47.107.85.10:8084/api/upload";
   // private String fileName = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //这里的第二个参数要和上面的第一个参数一样
        Notification notification = new NotificationCompat.Builder(this, "110")
                .setContentTitle("Http文件上传服务")
                .setContentText("已开启服务")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .build();

        startForeground(1, notification);
        Log.d(TAG, "onCreate: ");

        try {
            init();
            Toast.makeText(this, "启动HTPP上传服务成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "启动HTTP上传服务失败", Toast.LENGTH_SHORT).show();
        }

    }

    private void init() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startSend();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public UploadService() {
        super();
    }

    private void startSend() throws Exception {
        ArrayList<File> allFileName = null;
        while (true){
            allFileName = (ArrayList<File>) HttpUtils.getFilesAllName(dirPath);
            if (allFileName == null ||allFileName.isEmpty()){
                Log.d(TAG, "onCreate: cannot get file");
                Thread.sleep(3000); //3秒检查一次目录有没有文件
                continue;
            }
            Iterator iterator = allFileName.iterator();
            //Log.d(TAG, "onCreate: iterator is " + iterator.toString());
            while (iterator.hasNext()){
                file2upload = (File) iterator.next();
                Log.d(TAG, "onCreate: "+file2upload.toString());
               try {
                   Response response =  HttpUtils.upload(url,file2upload,CODE,TYPE);
                   String message = HttpUtils.parseJsonWithJsonObject(response);
                   if (message.equals("20000"))
                    {
                        // Toast.makeText(UploadService.this, "上传成功", Toast.LENGTH_LONG).show();
                        Boolean isDeleted = file2upload.delete();
                        Log.d(TAG, "doInBackground: code is " + message);
                        Log.d(TAG, "doInBackground: isDeleted " + isDeleted);
                    }else {
                        Log.d(TAG, "doInBackground: " + message);
                        //Thread.currentThread().interrupt();
                    }
               }catch (Exception e){
                   Log.d(TAG, "startSend:  网络异常 3秒后再试");
                   Thread.sleep(3000); //网络异常，不再尝试去发送已经遍历的文件
                   break;                     //回到检查目录是否有文件循环中
               }

            }

        }
    }
}