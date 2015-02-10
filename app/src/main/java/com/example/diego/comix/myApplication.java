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
    int[][] MPosition;
    int righe=3;
    int colonne = 3;


    Stripe(){
        scenes = new ArrayList<Scene>();
        //initialize scenes matrix
        // initialize M matrix
        MPosition = new int[righe][colonne];
        for (int i = 0; i < righe; i++) {
            for (int c = 0; c < colonne; c++) {
                MPosition[i][c]=0;
            }
        }


    }

}

class Scene{
    Bitmap bmp_shot;
    int Nheight;
    int Nwidht;
    int id_scene;
    Stripe myS;
    List<fumetto> fumetti;


    Scene(int id_scene, Stripe myS){
        fumetti = new ArrayList<fumetto>();
        this.myS=myS;
        calcSize();
        addFumetto();
    }

    void calcSize(){
        //calc size
        Nheight=0;
        Nwidht=0;
        int Ncell=0;
        for (int i = 0; i < myS.righe; i++) {
            int NwidhtNew=0;
            for (int c = 0; c < myS.colonne; c++) {
                if (myS.MPosition[i][c]==id_scene) {
                    Ncell++;
                    NwidhtNew++;
                    if (NwidhtNew>Nwidht){Nwidht=NwidhtNew;}
                }
            }
        }
        Nheight=(int)(Ncell/Nwidht);
    }

    void setShot(Bitmap bmp){
        this.bmp_shot=bmp;
    }

    void addFumetto(){
        fumetto myfumetto = new fumetto();
        this.fumetti.add(myfumetto);
    }
}


class fumetto{
    int xi, xf;
    int yi, yf;
    float xi_r,xf_r;
    float yi_r,yf_r;
    String testo;

    fumetto(){
        this.testo="prova";
        this.xi=50-20;
        this.xf=50+20;
        this.yi=50-10;
        this.yf=50+10;
    }

    void calcRealCoords(int w,int h){
        this.xf_r=this.xf*w/100;
        this.xi_r=this.xi*w/100;
        this.yf_r=this.yf*w/100;
        this.yi_r=this.yi*w/100;
    }

}









public class myApplication extends Application {

    public Stripe myStripe;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void CreateNewStripe(){

        int id_scene=1;
        myStripe = new Stripe();
        myStripe.MPosition[0][0]=id_scene;
        myStripe.MPosition[0][1]=id_scene;

        Scene myscene = new Scene(id_scene,myStripe);
        myStripe.scenes.add(myscene);

    }
}
