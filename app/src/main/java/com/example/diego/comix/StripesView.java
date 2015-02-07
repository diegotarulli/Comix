package com.example.diego.comix;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class StripesView extends View
{



    public StripesView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public void init(){setWillNotDraw(false);}

    float x,y;
    Bitmap bmp_shot=null;

    public void setFaces(){
        invalidate();
    }

    @Override
    protected void onDraw(Canvas c) {


        super.onDraw(c);
        c.drawColor(0x11880000);
        Paint paint1 = new Paint();
        if (bmp_shot!=null) {
            c.drawBitmap(bmp_shot, (float) 0, (float) 0, paint1);
        }
        c.drawCircle((float) x*10,(float)y*10,(float)100,paint1);
        c.drawCircle(50,50,(float)10,paint1);


    }

    public void setBmp(Bitmap bmp){
        this.bmp_shot = bmp;
        invalidate();
    }


}
