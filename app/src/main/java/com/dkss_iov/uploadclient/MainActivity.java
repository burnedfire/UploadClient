package com.dkss_iov.uploadclient;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView tv_info = null;
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, UploadService.class));
        tv_info = findViewById(R.id.tv_info);
        String filePath = "";
        List<File> files = HttpUtils.getFilesAllName("/mnt/sdcard/FtpFilePath");
        if (files==null || files.isEmpty())
        {
            tv_info.setText("no file");
        }else {
            Iterator iterator = files.iterator();
            while (iterator.hasNext()){
                String fileName = iterator.next().toString();
                filePath += (fileName + "\n");
            }
            tv_info.setText(filePath);
        }

    }
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, UploadService.class));
    }
}
