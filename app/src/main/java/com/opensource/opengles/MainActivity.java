package com.opensource.opengles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;

import com.opensource.opengles.camera.CameraActivity;
import com.opensource.opengles.render.ViewActivity;

public class MainActivity extends AppCompatActivity {

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
        findViewById(R.id.btn_1).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        });

    }
}