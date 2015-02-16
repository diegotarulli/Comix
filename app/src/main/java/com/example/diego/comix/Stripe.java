package com.example.diego.comix;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diego on 11/02/2015.
 */
class Stripe {

    List<Scene> scenes;
    int[][] MPosition;
    int righe;
    int colonne;


    Stripe() {

        scenes = new ArrayList<Scene>();
        //initialize scenes matrix
        // initialize M matrix
        righe = 1;
        colonne = 1;
        MPosition = new int[righe][colonne];
        for (int i = 0; i < righe; i++) {
            for (int c = 0; c < colonne; c++) {
                MPosition[i][c] = 0;
            }
        }
    }


    public int getLastIdScene() {
        int LastId = 0;
        for (int i = 0; i < righe; i++) {
            for (int c = 0; c < colonne; c++) {
                if (MPosition[i][c] > LastId) {
                    LastId = MPosition[i][c];
                }
            }
        }
        return LastId;
    }


    public void setMPositionSize(int Nrighe, int Ncolonne) {



        // Expand row or column of new matrix
        int[][] newMPosition = new int[Nrighe][Ncolonne];
        for (int i = 0; i < Nrighe; i++){
          for (int c = 0; c < Ncolonne; c++) {
              if (i > righe - 1 || c > colonne - 1) {
                  newMPosition[i][c] = 0;
              } else {
                  newMPosition[i][c] = MPosition[i][c];
              }
          }
        }
        MPosition=newMPosition;
        //update attributes
        righe= Nrighe;
        colonne=Ncolonne;
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
                        //scenes.get(is_found).calcSize();

                    }

                }
            }
        }
    }




    public void calcScenesSize(){

        float Nheight;
        float Nwidht;
        int cStart;
        int rStart;
        int id_scene;

        // for every scenes..
        for (int iSc=0; iSc<scenes.size(); iSc++) {
            //calc size based on the cell occupied in the MPosition
            Nheight = 0;
            Nwidht = 0;
            cStart = colonne;
            rStart = righe;
            id_scene=scenes.get(iSc).id_scene;

            int Ncell = 0;
            for (int i = 0; i < righe; i++) {
                int NwidhtNew = 0;
                for (int c = 0; c < colonne; c++) {
                    if (MPosition[i][c] == id_scene) {
                        if (i < rStart) {
                            scenes.get(iSc).rStart = i;
                        }
                        if (c < cStart) {
                            scenes.get(iSc).cStart = c;
                        }
                        Ncell++;
                        NwidhtNew++;
                        if (NwidhtNew > Nwidht) {
                            Nwidht = NwidhtNew;
                        }
                    }
                }
            }
            Nheight = (int) (Ncell / Nwidht);

            scenes.get(iSc).Nheight = Nheight / righe;
            scenes.get(iSc).Nwidht = Nwidht / colonne;
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

        addFumetto();
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

    void calcRealCoords(float w,float h){
        this.xf_r=this.xf*w/100;
        this.xi_r=this.xi*w/100;
        this.yf_r=this.yf*w/100;
        this.yi_r=this.yi*w/100;
    }

}

