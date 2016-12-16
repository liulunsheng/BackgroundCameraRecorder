package com.sth.camerabackgroundrecorder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sth.camerabackgroundrecorder.R;
import com.sth.camerabackgroundrecorder.service.BackgroundWorkService;
import com.sth.camerabackgroundrecorder.util.Assist;
import com.sth.camerabackgroundrecorder.util.CameraHelper;
import com.sth.camerabackgroundrecorder.util.Tools;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button start_recording, stop_recording;
    private CameraHelper mCamera = null;
    private boolean mCameraOpenSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFinishOnTouchOutside(false);
        start_recording = (Button) findViewById(R.id.start_recording);
        start_recording.setOnClickListener(this);
        stop_recording = (Button) findViewById(R.id.stop_recording);
        stop_recording.setOnClickListener(this);
        if (Assist.isRecording) {
            start_recording.setEnabled(false);
            stop_recording.setEnabled(true);
        } else {
            start_recording.setEnabled(true);
            stop_recording.setEnabled(false);
        }
        if (!Assist.isRecording) {
            mCamera = new CameraHelper(this);
            mCameraOpenSuccess = mCamera.openCamera();

            Log.i("cai", "MainActivity ---->open camera " + mCameraOpenSuccess);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_recording: {
                if (Assist.isRecording) {
                    Toast.makeText(this, "正在后台录像!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                if (!mCameraOpenSuccess) {
                    Toast.makeText(this, "相机打开失败!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mCamera != null) {
                    mCamera.closeCamera();
                }
                Intent service = new Intent(MainActivity.this, BackgroundWorkService.class);
                startService(service);
                //Assist.isTake_Picture = false;
                //Tools.sendBroadcast(this, Assist.FOREGROUND_TO_BACKGROUND, true);
                finish();
            }
            break;
            case R.id.stop_recording: {
                Tools.sendBroadcast(this, Assist.CANCEL);
            }
            finish();
            break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("cai", "MainActivity ---->close camera");
        if (mCamera != null)
            mCamera.closeCamera();
    }
}
