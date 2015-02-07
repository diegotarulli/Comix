package com.example.diego.comix;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Diego on 02/02/2015.
 */
public class OverlayView extends View {

    private int screenW;
    private int screenH;
    private float tXdown;
    private float tYdown;
    private float tXmove;
    private float tYmove;
    private float tXup=0;
    private float tYup=0;

    public OverlayView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public void init(){setWillNotDraw(false);}
    Preview preview;


    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //This event-method provides the real dimensions of this custom view.
        screenW = w;
        screenH = h;

    }





    //***************************************
    //*************  TOUCH  *****************
    //***************************************
    @Override
    public synchronized boolean onTouchEvent(MotionEvent ev) {

        // is able to call the method of original preview object.
        preview.touchEvent(ev);
        
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                tXdown = ev.getX();
                tYdown = ev.getY();


                break;
            }

            case MotionEvent.ACTION_MOVE: {
                tXmove = ev.getX();
                tYmove = ev.getY();

                break;
            }

            case MotionEvent.ACTION_UP:
                tXup = ev.getX();
                tYup = ev.getY();
                invalidate();
                break;
        }
        return true;
    }

    public void setPreview(Preview p){
        this.preview = p;

    }

    @Override
    protected void onDraw(Canvas c) {

        super.onDraw(c);
        c.drawColor(0x55880000);
        Paint paint1 = new Paint();
        c.drawCircle(screenW/2,screenH/2,(float)10,paint1);
        c.drawCircle(tXup,tYup,10,paint1);
    }



}
