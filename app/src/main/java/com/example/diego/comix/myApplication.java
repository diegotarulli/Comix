package com.example.diego.comix;

import android.app.Application;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diego on 04/02/2015.
 */

class Stripe{
    List<Scene> scenes;

    Stripe(){
        scenes = new ArrayList<Scene>();
    }

}

class Scene{
    Bitmap bmp_shot;

    void setShot(Bitmap bmp){
        this.bmp_shot=bmp;
    }
}



public class myApplication extends Application {

    public Stripe myStripe;

    @Override
    public void onCreate() {
        super.onCreate();




    }

    public void CreateNewStripe(){
        myStripe = new Stripe();
        Scene myscene = new Scene();
        myStripe.scenes.add(myscene);

    }
}
