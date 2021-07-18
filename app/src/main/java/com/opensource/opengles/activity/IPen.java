package com.opensource.opengles.activity;

import android.view.MotionEvent;
import android.view.View;

public interface IPen {
     void downEvent(View pageview, MotionEvent event);
     void moveEvent(View pageview, MotionEvent event);
     void upEvent(MotionEvent event);
     void drawPen(View pageview);
     void rest();
}
