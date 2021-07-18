package com.opensource.opengles.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;

import com.opensource.opengles.activity.IPen;

import java.util.ArrayDeque;

public class PagerView extends View {
    private Bitmap bitmap;
    private Canvas canvas;
    private ArrayDeque<IPen> mDraPenArrayDeque;
    private ArrayDeque<IPen> mmBackArrayDeque;
    private Paint mClearPaint;

    public PagerView(Context context) {
        super(context);
        initPaint();
    }

    private void initPaint() {
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void undo(PagerView pagerView){
        if (!mDraPenArrayDeque.isEmpty()) {

            canvas.drawPaint(mClearPaint);
            mmBackArrayDeque.push(mDraPenArrayDeque.pop());

            if(mDraPenArrayDeque.descendingIterator().hasNext()){
                IPen pen = mDraPenArrayDeque.descendingIterator().next();
                pen.drawPen(pagerView);
            }
            pagerView.invalidate();
        }
    }
}
