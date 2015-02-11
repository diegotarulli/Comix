package com.example.diego.comix;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import net.sourceforge.opencamera.R;

import java.util.ArrayList;


public class MainActivity extends Activity {






    public static StripesView myStripesView;
    private int current_orientation = 0;
    private OrientationEventListener orientationEventListener = null;

    protected myApplication app;
    ArrayList<StripesView> StripeViews;

    ZoomableViewGroup myZoomableViewGroup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the application instance
        app = (myApplication)getApplication();
        app.setMainAct(this);

        StripeViews = new ArrayList<StripesView>();
        // get layout
        myZoomableViewGroup = (ZoomableViewGroup)findViewById(R.id.myZoomableViewGroup);
        myZoomableViewGroup.setMainAct(this);

        //FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(150, 150);

        /*
        //????
        ViewTreeObserver vto = myZoomableViewGroup.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                myZoomableViewGroup.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int viewWidth = myZoomableViewGroup.getMeasuredWidth();

            }
        });

        */




        /*
        // create view for stripes visualization
        myStripesView = (StripesView)findViewById(R.id.myStripesView);
        myStripesView.init();
        myStripesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                //startActivity(intent);
            }
        });
        */


        app.CreateNewStripe();

        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                MainActivity.this.onOrientationChanged(orientation);
            }
        };




    }


    public void AddViews(){
        int id_scene;
        boolean trovato;
        int iSV_tro;


        for (int iS =0; iS<app.myStripe.scenes.size(); iS++){
            id_scene=app.myStripe.scenes.get(iS).id_scene;

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

            if (trovato==true){

            }else{
                // add new StripeViews
                //final StripesView mySV = new StripesView(this.getApplicationContext());

                StripeViews.add(iS,new StripesView(this.getApplicationContext()));
                StripeViews.get(iS).id_scene=id_scene;
                StripeViews.get(iS).setScene(app.myStripe.scenes.get(iS));

                myZoomableViewGroup.addView(StripeViews.get(iS));

                // Other attributes of backgroundImageView to modify
                FrameLayout.LayoutParams mySVParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT
                );
                mySVParams.setMargins(4, 5,4, 5);
                mySVParams.gravity = Gravity.LEFT;
                mySVParams.gravity = Gravity.BOTTOM;
                StripeViews.get(iS).setBackgroundColor(Color.parseColor("#44228888"));
                StripeViews.get(iS).setLayoutParams(mySVParams);


            }


            // try to move the view
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) StripeViews.get(iS).getLayoutParams();

            layoutParams.leftMargin = (iS+1)*500;
            layoutParams.topMargin = 10;
            layoutParams.rightMargin = -250;
            layoutParams.bottomMargin = -250;
            StripeViews.get(iS).setPadding(100,100,100,100);
            StripeViews.get(iS).setLayoutParams(layoutParams);

            myZoomableViewGroup.invalidate();



        }

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
            myStripesView.setStripe(app.myStripe);
        }

    }









    }




