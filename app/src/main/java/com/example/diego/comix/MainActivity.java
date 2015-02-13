package com.example.diego.comix;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import net.sourceforge.opencamera.R;

import java.util.ArrayList;


public class MainActivity extends Activity {



    public static SView mySView;
    private int current_orientation = 0;
    private OrientationEventListener orientationEventListener = null;

    protected myApplication app;
    ArrayList<SView> StripeViews;
    FrameLayout myFrameLayout;

    public Stripe myStripe;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the application instance
        app = (myApplication)getApplication();
        app.setMainAct(this);

        myFrameLayout = (FrameLayout)findViewById(R.id.myFrameLayout);


        StripeViews = new ArrayList<SView>();

        // create new stripe
        myStripe=new Stripe();
        int Idx = myStripe.getLastIdScene();
        // TODO the MPosition should be inside Stripe class
        myStripe.MPosition[0][0]=Idx;
        Scene myscene = new Scene(Idx,myStripe);
        myStripe.scenes.add(myscene);


        //create images
        StripeViews.add(0,new SView(this.getApplicationContext()));
        FrameLayout.LayoutParams Lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT
        );
        Lp.setMargins(250, 50,4, 5);
        Lp.gravity = Gravity.LEFT;
        Lp.gravity = Gravity.TOP;
        myFrameLayout.addView(StripeViews.get(0),0,Lp);



        StripeViews.add(1,new SView(this.getApplicationContext()));
        Lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT
        );
        Lp.setMargins(0, 0,4, 5);
        Lp.gravity = Gravity.LEFT;
        Lp.gravity = Gravity.TOP;
        myFrameLayout.addView(StripeViews.get(1),1,Lp);



        // save stripe to Application
        // TODO serve??
        app.setStrip(myStripe);



        //init touch
        mScaleDetector = new ScaleGestureDetector(this.getApplicationContext(), new ScaleListener());
        mTranslateMatrix.setTranslate(0, 0);
        mScaleMatrix.setScale(1, 1);


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

            Iinvalidate();
            //requestLayout();


            return true;
        }
    }



    public void Iinvalidate(){
        Log.i("i", "invalidate CALLED !!!!!!");
        Log.i("i","FattoreScala : " + mScaleFactor);
        Log.i("i","mFocusX : " + mFocusX);
        Log.i("i","mFocusY : " + mFocusY);
        //TranslateAnimation anim=new TranslateAnimation(0, 0, 0, 40);
        //anim.setFillAfter(true);
        //anim.setDuration(1000);
        //StripeViews.get(1).startAnimation(anim);



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
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)StripeViews.get(1).getLayoutParams();
                params.topMargin += 100;
                params.leftMargin += 100;
                StripeViews.get(1).setLayoutParams(params);
            }
        });

        StripeViews.get(1).startAnimation(anim);



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

        Bitmap bmp_shot = app.myStripe.scenes.get(0).bmp_shot;
        if (bmp_shot!=null){
            mySView.setStripe(app.myStripe);
        }

    }






    public void CreateNewStripe(){

        int id_scene=1;
        myStripe = new Stripe();
        myStripe.MPosition[0][0]=id_scene;

        Scene myscene = new Scene(id_scene,myStripe);
        myStripe.scenes.add(myscene);

        //AddViews();

        // Procedure to add a scene
        // ask for last id
        int LastId = myStripe.getLastIdScene();


        /*
        // Expand row or column of new matrix
        int Nrighe = myStripe.righe;
        int Ncolonne = myStripe.colonne+1;
        int [][] newMPosition = new int[Nrighe][Ncolonne];
        for (int i = 0; i < Nrighe; i++) {
            for (int c = 0; c < Ncolonne; c++) {
                if (i>myStripe.righe-1||c>myStripe.colonne-1){
                    newMPosition[i][c]=0;
                }else{
                    newMPosition[i][c]=myStripe.MPosition[i][c];
                }

            }
        }

        // Write new cell id
        newMPosition[0][1]=LastId+1;

        // Add scene to matrix and calc again all the sizes
        myStripe.addCellScene(newMPosition);

        // Update all the views
        AddViews();
        */



    }



/**
    public void AddViews(){
        int id_scene;
        boolean trovato;
        int iSV_tro;


        for (int iS =0; iS< myStripe.scenes.size(); iS++){
            id_scene=myStripe.scenes.get(iS).id_scene;

            // search id_scene in StripeViews
            trovato = false;
            iSV_tro = 0;
            for (int iSV=0; iSV<StripeViews.size(); iSV++){
                if (id_scene==StripeViews.get(iSV).id_scene){
                    trovato=true;
                    iSV_tro = iSV;
                    break;
                }
            }

            if (trovato==false){
                // add new StripeViews
                StripeViews.add(iS,new StripesView(this.getApplicationContext()));
                StripeViews.get(iS).id_scene=id_scene;
                StripeViews.get(iS).setScene(myStripe.scenes.get(iS));
                myZoomableViewGroup.addView(StripeViews.get(iS));
            }

            //myZoomableViewGroup.invalidate();

            // Change view property
            StripeViews.get(iS).setBackgroundColor(Color.WHITE);



            //ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) myZoomableViewGroup.getLayoutParams();
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT,ViewGroup.MarginLayoutParams.WRAP_CONTENT);
            lp.leftMargin=200;
            StripeViews.get(iS).setLayoutParams(lp);




            //ZoomableViewGroup.MarginLayoutParams layoutParams=new ZoomableViewGroup.MarginLayoutParams(ZoomableViewGroup.MarginLayoutParams.WRAP_CONTENT,ZoomableViewGroup.MarginLayoutParams.WRAP_CONTENT);
            //layoutParams.setMargins(200, 100, 10, 10);
            //StripeViews.get(iS).setLayoutParams(layoutParams);

            // try to move the view
            // Other attributes of backgroundImageView to modify


            //myZoomableViewGroup.invalidate();



        }

    }
 */





    }




