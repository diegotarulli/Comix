package com.example.diego.comix;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Matrix;

public class StripesView extends View
{



    public StripesView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public void init(){setWillNotDraw(false);}

    float x,y;
    Bitmap bmp_shot=null;
    int width;
    int height;

    public void setFaces(){
        invalidate();
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 100;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);



        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }


        height = width;

        //MUST CALL THIS
        setMeasuredDimension(width, height);
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


    }

    public void setBmp(Bitmap bmp){
        this.bmp_shot = bmp;
        invalidate();
    }


}
