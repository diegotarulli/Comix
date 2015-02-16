package com.example.diego.comix;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import net.sourceforge.opencamera.R;

import java.util.ArrayList;


public class MainActivity extends Activity {


    public static final int EDIT = 10;

    private int current_orientation = 0;
    private OrientationEventListener orientationEventListener = null;

    protected myApplication app;
    ArrayList<SView> SViews;
    FrameLayout myFrameLayout;

    public Stripe myStripe;

    int FrameWidht;
    int FrameHeight;

    // attributes for pan and zoom
    float x_center;
    float y_center;
    float scale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the application instance
        app = (myApplication)getApplication();
        app.setMainAct(this);

        myFrameLayout = (FrameLayout)findViewById(R.id.myFrameLayout);

        SViews = new ArrayList<SView>();


        // ######## SCENE 1 ################
        // create new stripe
        myStripe=new Stripe();
        int Idx = myStripe.getLastIdScene()+1;
        // TODO the MPosition should be inside Stripe class
        myStripe.MPosition[0][0]=Idx;
        Scene myscene = new Scene(Idx,myStripe);
        myStripe.scenes.add(myscene);
        myStripe.calcScenesSize();

        // for each scene inside stripe SView has to be done
        //create images (SView obj)
        SViews.add(0,new SView(this.getApplicationContext()));
        FrameLayout.LayoutParams Lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT
        );
        Lp.setMargins(0, 0,0, 0);
        Lp.gravity = Gravity.LEFT;
        Lp.gravity = Gravity.TOP;
        SViews.get(Idx-1).setScene(myStripe.scenes.get(Idx-1));
        SViews.get(Idx-1).id_scene=Idx;
        SViews.get(Idx-1).setMA(this);
        myFrameLayout.addView(SViews.get(Idx-1),Idx-1,Lp);



        // ######## SCENE 2 ################
        // create another scene on the stripe
        Idx = myStripe.getLastIdScene()+1;
        myStripe.setMPositionSize(1,2);
        myStripe.MPosition[0][1]=Idx;
        myscene = new Scene(Idx,myStripe);
        myStripe.scenes.add(myscene);
        myStripe.calcScenesSize();

        // for each scene inside stripe SView has to be done
        //create images (SView obj)
        SViews.add(Idx-1,new SView(this.getApplicationContext()));
        Lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT
        );
        Lp.setMargins(0, 0,0, 0);
        Lp.gravity = Gravity.LEFT;
        Lp.gravity = Gravity.TOP;
        SViews.get(Idx-1).setScene(myStripe.scenes.get(Idx-1));
        SViews.get(Idx-1).id_scene=Idx;
        SViews.get(Idx-1).setMA(this);
        myFrameLayout.addView(SViews.get(Idx-1),Idx-1,Lp);






        // save stripe to Application
        // TODO serve??
        app.setStrip(myStripe);



        //init touch
        mScaleDetector = new ScaleGestureDetector(this.getApplicationContext(), new ScaleListener());
        mTranslateMatrix.setTranslate(0, 0);
        mScaleMatrix.setScale(1, 1);


    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        CalcSceneSizesPositions();

    }


    public void CalcSceneSizesPositions(){
        FrameWidht = myFrameLayout.getWidth();
        FrameHeight = myFrameLayout.getHeight();
        int h;
        int w;
        int x;
        int y;

        x_center=FrameWidht/2;
        y_center=FrameHeight/2;

        // set size and position on every scene
        for (int iS=0; iS<SViews.size(); iS++){

            //TODO:Attenzione a come calcola Nwidht e Nheight a partire da Mposition
            w=(int)(SViews.get(iS).scene.Nwidht*FrameWidht);
            h = (int)(w*SViews.get(iS).scene.Nheight/SViews.get(iS).scene.Nwidht);
            if (h>SViews.get(iS).scene.Nheight*FrameHeight) {
                h = (int)(SViews.get(iS).scene.Nheight*FrameHeight);
                w = (int)(h*SViews.get(iS).scene.Nwidht/SViews.get(iS).scene.Nheight);
            }
            x = (int)((float)(SViews.get(iS).scene.cStart)/(float)(myStripe.colonne)*FrameWidht);
            y = (int)((float)(SViews.get(iS).scene.rStart)/(float)(myStripe.righe)*FrameHeight);

            // set the default position and size attributes
            SViews.get(iS).orig_x = x;
            SViews.get(iS).orig_y = y;
            SViews.get(iS).orig_w = w;
            SViews.get(iS).orig_h = h;

            // set the actual position and size attributes
            SViews.get(iS).x = x;
            SViews.get(iS).y = y;
            SViews.get(iS).width = w;
            SViews.get(iS).height = h;

            // TODO: move only at initialization
            moveSizeScene(SViews.get(iS),x,y,w,h);
        }


    }

    public void moveSizeScene(SView Sv,int x,int y,int w,int h){
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)Sv.getLayoutParams();
        params.topMargin = y;
        params.leftMargin = x;
        params.width=w;
        params.height=h;
        Sv.setLayoutParams(params);

    }

    // pan and zoom method
    public void PanZoom(float x_center,float y_center,float scale){

        // for pan the initial coordinates of the Views are used

        float dX = FrameWidht/2-x_center;
        float dY = FrameHeight/2-y_center;
        int xN,yN,wN,hN;

        for (int iS=0; iS<SViews.size(); iS++){

            xN = (int) (SViews.get(iS).orig_x + dX + (SViews.get(iS).orig_x-x_center)*(scale-1));
            yN = (int) (SViews.get(iS).orig_y + dY+ (SViews.get(iS).orig_y-y_center)*(scale-1));
            wN = (int) (SViews.get(iS).orig_w*scale);
            hN = (int) (SViews.get(iS).orig_h*scale);
            moveSizeScene(SViews.get(iS), xN ,yN ,wN ,hN );

        }



    }








    private static final int INVALID_POINTER_ID = 1;
    private int mActivePointerId = INVALID_POINTER_ID;

    private float mScaleFactor = 1;
    private ScaleGestureDetector mScaleDetector;
    private Matrix mScaleMatrix = new Matrix();
    private Matrix mScaleMatrixInverse = new Matrix();

    private float mPosX;
    private float mPosY;
    private Matrix mTranslateMatrix = new Matrix();
    private Matrix mTranslateMatrixInverse = new Matrix();

    boolean FirstMove;
    private float mLastTouchX;
    private float mLastTouchY;

    private float[] mInvalidateWorkingArray = new float[6];
    private float[] mDispatchTouchEventWorkingArray = new float[2];
    private float[] mOnTouchEventWorkingArray = new float[2];

    private float mFocusY;
    private float mFocusX;

    private float[] scaledPointsToScreenPoints(float[] a) {
        mScaleMatrix.mapPoints(a);
        mTranslateMatrix.mapPoints(a);
        return a;
    }

    private float[] screenPointsToScaledPoints(float[] a){
        mTranslateMatrixInverse.mapPoints(a);
        mScaleMatrixInverse.mapPoints(a);
        return a;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDispatchTouchEventWorkingArray[0] = ev.getX();
        mDispatchTouchEventWorkingArray[1] = ev.getY();
        mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);
        ev.setLocation(mDispatchTouchEventWorkingArray[0],
                mDispatchTouchEventWorkingArray[1]);

        return super.dispatchTouchEvent(ev);

    }

    /*
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                FirstMove=true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE: {
                return true;
            }
        }

        return false;

    }
    */

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mOnTouchEventWorkingArray[0] = ev.getX();
        mOnTouchEventWorkingArray[1] = ev.getY();

        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray);

        ev.setLocation(mOnTouchEventWorkingArray[0], mOnTouchEventWorkingArray[1]);
        mScaleDetector.onTouchEvent(ev);

        // this is called if there is an ACTION DOWN inside a child, the onInterceptTouchEvent is true only for
        // sequent move event
        if (FirstMove) {
            mDispatchTouchEventWorkingArray[0] = ev.getX();
            mDispatchTouchEventWorkingArray[1] = ev.getY();
            mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);

            mLastTouchX = ev.getX();
            mLastTouchY = ev.getY();
            // Save the ID of this pointer
            mActivePointerId = ev.getPointerId(0);
            FirstMove=false;
        }


        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {

                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                // Save the ID of this pointer
                mActivePointerId = ev.getPointerId(0);
                break;

            }

            case MotionEvent.ACTION_MOVE: {

                // Find the index of the active pointer and fetch its position
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);
                mPosX=0;
                mPosY=0;
                mPosX = mLastTouchX-x;
                mPosY = mLastTouchY-y;


                mLastTouchX = x;
                mLastTouchY = y;

                Iinvalidate(false);
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                    Log.i("ddd", "" + ev.getY(newPointerIndex));
                }
                break;
            }
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            if (detector.isInProgress()) {
                mFocusX = detector.getFocusX();
                mFocusY = detector.getFocusY();
            }
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            mScaleMatrix.setScale(mScaleFactor, mScaleFactor,
                    mFocusX, mFocusY);
            mScaleMatrix.invert(mScaleMatrixInverse);
            Iinvalidate(true);
            //requestLayout();


            return true;
        }
    }


    public void Iinvalidate(boolean ActionScala){
        Log.i("i", "invalidate CALLED !!!!!!");
        Log.i("i","FattoreScala : " + mScaleFactor);
        Log.i("i","mFocusX : " + mFocusX);
        Log.i("i","mFocusY : " + mFocusY);
        Log.i("i","mPosX : " + mPosX);
        Log.i("i","mPosY : " + mPosY);
        x_center = x_center + mPosX;
        y_center = y_center + mPosY;
        if (ActionScala) {
            scale = mScaleFactor;
        }
        PanZoom(x_center,y_center,scale);

        //TODO non va qui...
        SViews.get(0).status=0;
        SViews.get(1).status=0;

        //TranslateAnimation anim=new TranslateAnimation(0, 0, 0, 40);
        //anim.setFillAfter(true);
        //anim.setDuration(1000);
        //StripeViews.get(1).startAnimation(anim);

        /* Animation - Can't move multiple views
        TranslateAnimation anim = new TranslateAnimation(0, 100, 0, 100);
        anim.setDuration(1000);
        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationRepeat(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation)
            {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)SViews.get(1).getLayoutParams();
                params.topMargin += 100;
                params.leftMargin += 100;
                SViews.get(1).setLayoutParams(params);
            }
        });
        SViews.get(1).startAnimation(anim);
        */



    }


    // This method is called from a touch event on SView
    public void RequestEditFromView(int id){

        Log.i("TAG","Requested EDIT status View "+id);


        // Ask Pan and Zoom Views
        // Calc translation of the views
        //(consider id = id_scene)
        x_center=SViews.get(id-1).orig_x+SViews.get(id-1).orig_w/2;
        y_center=SViews.get(id-1).orig_y+SViews.get(id-1).orig_h/2;
        // calc scale
        scale = (float)(FrameWidht)/SViews.get(id-1).orig_w;
        if ((float)(FrameHeight)/SViews.get(id-1).orig_h<(float)(FrameWidht)/SViews.get(id-1).orig_w){
            scale = (float)(FrameHeight)/SViews.get(id-1).orig_h;
        }
        scale = scale*9/10;


        PanZoom(x_center,y_center,scale);

        // change EDIT status of the SView
        SViews.get(id-1).status=EDIT;

        app.setCourrentIdScene(id);

    }








    private void onOrientationChanged(int orientation) {

        if( orientation == OrientationEventListener.ORIENTATION_UNKNOWN )
            return;
        int diff = Math.abs(orientation - current_orientation);
        if( diff > 180 )
            diff = 360 - diff;
        // only change orientation when sufficiently changed
        if( diff > 60 ) {
            orientation = (orientation + 45) / 90 * 90;
            orientation = orientation % 360;
            if( orientation != current_orientation ) {
                this.current_orientation = orientation;
                layoutUI();
            }
        }
    }



    public void layoutUI() {

        // new code for orientation fixed to landscape
        // the display orientation should be locked to landscape, but how many degrees is that?
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        // getRotation is anti-clockwise, but current_orientation is clockwise, so we add rather than subtract
        // relative_orientation is clockwise from landscape-left
        //int relative_orientation = (current_orientation + 360 - degrees) % 360;
        int relative_orientation = (current_orientation + degrees) % 360;
        int ui_rotation = (360 - relative_orientation) % 360;
        int align_left = RelativeLayout.ALIGN_LEFT;
        int align_right = RelativeLayout.ALIGN_RIGHT;
        int left_of = RelativeLayout.LEFT_OF;
        int right_of = RelativeLayout.RIGHT_OF;
        int above = RelativeLayout.ABOVE;
        int below = RelativeLayout.BELOW;
        int align_parent_left = RelativeLayout.ALIGN_PARENT_LEFT;
        int align_parent_right = RelativeLayout.ALIGN_PARENT_RIGHT;
        int align_parent_top = RelativeLayout.ALIGN_PARENT_TOP;
        int align_parent_bottom = RelativeLayout.ALIGN_PARENT_BOTTOM;

        /*{
            // we use a dummy button, so that the GUI buttons keep their positioning even if the Settings button is hidden (visibility set to View.GONE)
            //View view = findViewById(R.id.myStripesView);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)myStripesView.getLayoutParams();
            layoutParams.addRule(align_parent_left, 0);
            layoutParams.addRule(align_parent_right, RelativeLayout.TRUE);
            layoutParams.addRule(align_parent_top, RelativeLayout.TRUE);
            layoutParams.addRule(align_parent_bottom, 0);
            layoutParams.addRule(left_of, 0);
            layoutParams.addRule(right_of, 0);
            myStripesView.setLayoutParams(layoutParams);
            myStripesView.setRotation(ui_rotation);

            View view = findViewById(R.id.mytextView);
            layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
            layoutParams.addRule(align_parent_top, RelativeLayout.TRUE);
            layoutParams.addRule(align_parent_left, 0);
            layoutParams.addRule(left_of, R.id.gui_anchor);
            layoutParams.addRule(below, 0);
            view.setLayoutParams(layoutParams);
            view.setRotation(ui_rotation);

        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        layoutUI();

        // TODO implement!

        //update the bmp
        myStripe=app.myStripe;
        for (int iS=0; iS<myStripe.scenes.size(); iS++){
            SViews.get(iS).setShot(myStripe.scenes.get(iS).bmp_shot);
        }

        //Bitmap bmp_shot = app.myStripe.scenes.get(0).bmp_shot;
        //if (bmp_shot!=null){
        //    SView.setStripe(app.myStripe);
        //}


    }





    }




