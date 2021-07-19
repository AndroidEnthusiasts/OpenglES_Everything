package com.opensource.opengles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;

import com.opensource.opengles.activity.PaintActivity;
import com.opensource.opengles.camera.CameraActivity;
import com.opensource.opengles.camera.CameraActivity2;
import com.opensource.opengles.model.ModelLoadActivity;
import com.opensource.opengles.render.ViewActivity;
import com.opensource.opengles.ui_yida.FilterActivity;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("CAMERA_LIB");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        System.out.println("widthPixels=" + widthPixels);
        System.out.println("heightPixels=" + heightPixels);

        findViewById(R.id.btn_1).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_2).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity2.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_3).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FilterActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_4).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PaintActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_5).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ModelLoadActivity.class);
            startActivity(intent);
        });



    }
}