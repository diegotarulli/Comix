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
    int righe;
    int colonne;


    Stripe(){

        scenes = new ArrayList<Scene>();
        //initialize scenes matrix
        // initialize M matrix
        righe=1;
        colonne=1;
        MPosition = new int[righe][colonne];
        for (int i = 0; i < righe; i++) {
            for (int c = 0; c < colonne; c++) {
                MPosition[i][c]=0;
            }
        }
    }


    public int getLastIdScene(){
        int LastId = 0;
        for (int i = 0; i < righe; i++) {
            for (int c = 0; c < colonne; c++) {
                if (MPosition[i][c]>LastId){
                    LastId=MPosition[i][c];
                }
            }
        }
        return LastId;
    }

    public void addCellScene(int[][] newMPosition){
        // calc new size
        // assume that newMPosition is rectangular
        righe=newMPosition.length;
        colonne=newMPosition[0].length;
        MPosition=newMPosition;
        // Create scene based on new index or delete scene out of new matrix

        boolean foundScene;
        int is_found;
        for (int i = 0; i < (righe); i++) {
            for (int c = 0; c < (colonne); c++) {
                if (MPosition[i][c]>0){
                    // look for existing scene
                    foundScene=false;
                    is_found=0;
                    for (int i_s=0; i_s<scenes.size(); i_s++){
                        if (scenes.get(i_s).id_scene==MPosition[i][c]){
                            foundScene=true;
                            is_found = i_s;
                            break;
                        }
                    }
                    if (foundScene==false){
                        // add new scene to stripe anc calc its size
                        Scene myscene = new Scene(MPosition[i][c],this);
                        scenes.add(myscene);
                    }else{
                        // re-calc founded scene size
                        scenes.get(is_found).calcSize();

                    }

                }
            }
        }

    }



}

class Scene{
    Bitmap bmp_shot;
    float Nheight;
    float Nwidht;
    int rStart;
    int cStart;
    int id_scene;
    boolean ContPicture;
    boolean ContText;
    Stripe myS;
    List<fumetto> fumetti;


    Scene(int id_scene, Stripe myS){
        fumetti = new ArrayList<fumetto>();
        this.myS=myS;
        this.id_scene=(id_scene);

        calcSize();
        addFumetto();
    }

    void calcSize(){
        //calc size based on the cell occupied in the MPosition
        Nheight=0;
        Nwidht=0;
        cStart=myS.colonne;
        rStart=myS.righe;
        int Ncell=0;
        for (int i = 0; i < myS.righe; i++) {
            int NwidhtNew=0;
            for (int c = 0; c < myS.colonne; c++) {
                if (myS.MPosition[i][c]==id_scene) {
                    if (i<rStart){rStart=i;}
                    if (c<cStart){cStart=c;}
                    Ncell++;
                    NwidhtNew++;
                    if (NwidhtNew>Nwidht){Nwidht=NwidhtNew;}
                }
            }
        }
        Nheight=(int)(Ncell/Nwidht);

        Nheight = Nheight/myS.righe;
        Nwidht = Nwidht/myS.colonne;
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
    public MainActivity MainAct;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void setMainAct(MainActivity Act){
        this.MainAct=Act;
    }


    public void CreateNewStripe(){

        int id_scene=1;
        myStripe = new Stripe();
        myStripe.MPosition[0][0]=id_scene;

        Scene myscene = new Scene(id_scene,myStripe);
        myStripe.scenes.add(myscene);

        MainAct.AddViews();

        // Procedure to add a scene
        // ask for last id
        int LastId = myStripe.getLastIdScene();

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
        MainAct.AddViews();



    }
}
