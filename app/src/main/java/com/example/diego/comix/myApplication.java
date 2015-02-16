package com.example.diego.comix;

import android.app.Application;

/**
 * Created by Diego on 04/02/2015.
 */


public class myApplication extends Application {

    public Stripe myStripe;
    public MainActivity MainAct;

    private int CourrentIdScene;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void setCourrentIdScene(int id){this.CourrentIdScene=id;}

    public int getCourrentIdScene(){return this.CourrentIdScene;}

    public void setMainAct(MainActivity Act){
        this.MainAct=Act;
    }


    public void setStrip(Stripe myStripe) {
        this.myStripe=myStripe;

    }
}
