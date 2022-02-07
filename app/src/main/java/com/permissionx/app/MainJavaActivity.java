package com.permissionx.app;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.permissionx.guolilndev.lincolnct.permission.PermissionRequestBuilder;

public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);
        Button makeRequestBtn = findViewById(R.id.makeRequestBtn);
        makeRequestBtn.setOnClickListener(view ->
                PermissionRequestBuilder.newInstance(MainJavaActivity.this)
                        .requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setRequestReason("请求以上权限是必须的")
                        .setDeniedTips("请授权以上权限否则无法使用")
                        .request((allGranted, grantedList, deniedList) -> {
                            if (allGranted) {
                                Toast.makeText(MainJavaActivity.this, "All permissions are granted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainJavaActivity.this, "The following permissions are denied：" + deniedList, Toast.LENGTH_SHORT).show();
                            }
                        }));
    }
}