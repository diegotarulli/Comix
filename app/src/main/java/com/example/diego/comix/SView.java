package com.example.diego.comix;


import android.content.Context;
import android.content.Intent;
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

public class SView extends View
{

    public static final int EDIT = 10;
    Bitmap bmp_shot=null;

    float x;
    float y;
    float width;
    float height;
    int orig_x;
    int orig_y;
    int orig_w;
    int orig_h;


    boolean LockSurface = false;
    Scene scene;
    int id_scene;
    List<fumetto> fumetti;
    int status;
    Context ctx;

    MainActivity myMA;

    public void setMA(MainActivity MA){
        this.myMA=MA;
    }

    public SView(Context context, AttributeSet attrs){
        super(context,attrs);
        Context ctx;
    }

    public SView(Context context){
        super(context);
        ctx = context;
    }



    public void init(){setWillNotDraw(false);}

    private static final String TAG = "StripesView";

    public void setFaces(){
        invalidate();
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth=200;
        int desiredHeight=200;


        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

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

        width = parentWidth;

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

        height = parentHeight;

        // TODO check this? for square cells?
        //height = width;

        //MUST CALL THIS
        setMeasuredDimension((int)(width), (int)(height));
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

                if (this.status==EDIT){
                    OpenCameraActivity();
                }else{
                    this.myMA.RequestEditFromView(this.id_scene);
                    this.status=EDIT;
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

    public void OpenCameraActivity(){
        // TODO.. remove this
        Intent intent = new Intent(ctx, CreateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    @Override
    protected void onDraw(Canvas c) {


        super.onDraw(c);
        c.drawColor(0x11880000);
        Paint paint1 = new Paint();


        // border
        paint1.setColor(0x33990000);
        RectF r = new RectF(0, 0, this.width-2, this.height-2);
        float rad=50;
        c.drawRoundRect(r, rad, rad, paint1);


        if (scene.bmp_shot!=null) {

            // scale the bitmap
            float scaleWidth = ((float) width) / scene.bmp_shot.getWidth();
            float scaleHeight = ((float) height) / scene.bmp_shot.getHeight();
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(scene.bmp_shot, 0, 0, scene.bmp_shot.getWidth(), scene.bmp_shot.getHeight(), matrix, false);
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
                r = new RectF(f.xi_r, f.yi_r, f.xf_r, f.yf_r);
                rad=50;
                c.drawRoundRect(r, rad, rad, paint_f);
                paint1.setTextSize(20);
                c.drawText(f.testo+" SC"+this.scene.id_scene,f.xi_r+rad*2, f.yi_r+rad*2,paint1);
            }
        }



    }

    // TODO delete this method
    public void setStripe(Stripe stripe){
        this.bmp_shot = stripe.scenes.get(0).bmp_shot;
        this.scene=stripe.scenes.get(0);
        this.fumetti = stripe.scenes.get(0).fumetti;
        fumetti.get(0).calcRealCoords(width,height);
        invalidate();
    }

    // keep this
    public void setScene(Scene myScene){
        this.scene=myScene;
    }
    // keep this
    public void setShot(Bitmap bmp){
        this.scene.bmp_shot=bmp;
    }



}
