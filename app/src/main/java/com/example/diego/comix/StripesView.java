package com.example.diego.comix;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class StripesView extends View
{



    public StripesView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public StripesView(Context context){
        super(context);
    }

    public void init(){setWillNotDraw(false);}

    private static final String TAG = "StripesView";

    float x,y;
    Bitmap bmp_shot=null;
    int width;
    int height;
    boolean LockSurface = false;
    Scene scene;
    int id_scene;
    List<fumetto> fumetti;

    public void setFaces(){
        invalidate();
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 400;
        int desiredHeight = 400;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        desiredHeight=(int)(scene.Nheight*parentHeight);
        desiredWidth = (int)(desiredHeight*scene.Nwidht/scene.Nheight);
        if (desiredWidth/scene.Nwidht>parentWidth) {
            desiredWidth = (int)(scene.Nwidht * parentWidth);
            desiredHeight = (int) (desiredWidth * scene.Nheight / scene.Nwidht);
        }


        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = parentWidth;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, parentWidth);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = parentHeight;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, parentHeight);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }


        height = width;

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }



    //***************************************
    //*************  TOUCH  *****************
    //***************************************

    @Override
    public synchronized boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                // Use for disallow the parent  to get the control (scroll actions) of the touch event
                if (LockSurface){
                    this.getParent().requestDisallowInterceptTouchEvent(true);
                }else {
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                break;
            }

            case MotionEvent.ACTION_UP:

                Log.i(TAG, "Pressed and released on surface");
                if (!LockSurface) {
                    LockSurface = true;
                    GUIEditScene();
                }else{
                    LockSurface=false;
                }

                TouchUp(ev.getX(), ev.getY());
                break;
        }
        return true;
    }


    private void GUIEditScene(){
        Log.i(TAG, "Surface locked, GUIEditScene");
    }

    private void TouchUp(float x,float y){

    }


    @Override
    protected void onDraw(Canvas c) {


        super.onDraw(c);
        c.drawColor(0x11880000);
        Paint paint1 = new Paint();
        if (bmp_shot!=null) {

            // scale the bitmap
            float scaleWidth = ((float) width) / bmp_shot.getWidth();
            float scaleHeight = ((float) height) / bmp_shot.getHeight();
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bmp_shot, 0, 0, bmp_shot.getWidth(), bmp_shot.getHeight(), matrix, false);
            c.drawBitmap(resizedBitmap, (float) 0, (float) 0, paint1);

        }
        c.drawCircle((float) x*10,(float)y*10,(float)100,paint1);
        c.drawCircle(50,50,(float)10,paint1);

        /*** Draw fumetto ***/
        if (this.scene!=null) {
            if (this.scene.fumetti.size()>0) {
                Paint paint_f = new Paint();
                paint_f.setColor(0xffffffff);
                fumetto f = this.scene.fumetti.get(0);
                RectF r = new RectF(f.xi_r, f.yi_r, f.xf_r, f.yf_r);
                float rad=50;
                c.drawRoundRect(r, rad, rad, paint_f);
                paint1.setTextSize(20);
                c.drawText(f.testo,f.xi_r+rad*2, f.yi_r+rad*2,paint1);
            }
        }

    }

    public void setStripe(Stripe stripe){
        this.bmp_shot = stripe.scenes.get(0).bmp_shot;
        this.scene=stripe.scenes.get(0);
        this.fumetti = stripe.scenes.get(0).fumetti;
        fumetti.get(0).calcRealCoords(width,height);
        invalidate();
    }

    public void setScene(Scene myScene){
        this.scene=myScene;
    }


}
