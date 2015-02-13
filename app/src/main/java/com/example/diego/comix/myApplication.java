package com.example.diego.comix;

import android.app.Application;

/**
 * Created by Diego on 04/02/2015.
 */


public class myApplication extends Application {

    public Stripe myStripe;
    public MainActivity MainAct;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void setMainAct(MainActivity Act){
        this.MainAct=Act;
    }


    public void setStrip(Stripe myStripe) {
        this.myStripe=myStripe;

    }
}
