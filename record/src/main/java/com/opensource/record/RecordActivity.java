package com.opensource.record;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.opensource.record.camera.CameraPreviewView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordActivity extends AppCompatActivity {
    private static String TAG = RecordActivity.class.getSimpleName();

    private CameraPreviewView cameraView;
    private Button btnRecord;

//    private MediaEncoder mediaEncodec;
    private boolean finish = false;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    static {
        System.loadLibrary("record-lib");
    }

    private void start() {
        finish = false;
        btnRecord.setText("正在录制...");
        btnRecord.setTextColor(Color.RED);
        //准备写入数据
//        if (mediaEncodec == null) {
//            Log.d(TAG, "textureid is " + cameraView.getTextureId());
//            try {
//                File parent = new File(Environment.getExternalStorageDirectory().getPath());
//                if (!parent.exists()) {
//                    parent.mkdirs();
//                }
//                String videoPath = Environment.getExternalStorageDirectory().getPath() + "/video" + simpleDateFormat.format(new Date())
//                        + ".mp4";
//                File file = new File(videoPath);
//                if (!file.exists()) {
//                    file.createNewFile();
//                }
//                //获得OpenGL的FBO渲染的纹理id，通过共享纹理id的方式将图像数据写入MediaCodec
//               //todo
//
//            } catch (IOException e) {
//                Log.d(TAG, "IOException is : " + e);
//            }
//        }
//        startRecord();
    }

    private void stop() {
//        stopRecord();
        finish = true;
        //停止写入数据
//        mediaEncodec.stopRecord();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnRecord.setText("开始录制");
                btnRecord.setTextColor(Color.BLACK);
            }
        });
//        mediaEncodec = null;
    }

    /**
     * 提供给native层调用(不可混淆)
     *
     * @param pcmData：麦克风获取到的音频数据
     */
    @Keep
    void onPcmDataInput(byte[] pcmData) {
        if (finish) {
            return;
        }
        Log.d("OpenSlDemo", "onPcmDataInput pcmData size:" + pcmData.length);
//        if (mediaEncodec != null) {
//            mediaEncodec.putPcmData(pcmData, pcmData.length);
//        }
    }

//    native void startRecord();
//
//    native void stopRecord();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        cameraView = findViewById(R.id.cameraview);
        btnRecord = findViewById(R.id.btn_record);

    }

    public void record(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraView != null) {
            cameraView.onDestroy();
        }
    }
}