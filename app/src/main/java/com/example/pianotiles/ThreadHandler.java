package com.example.pianotiles;

import java.util.ArrayList;
import java.util.Random;

public class ThreadHandler extends java.lang.Thread{
    protected UIThreadHandler UIThread;
    protected int FRAME_CHANGE_RATE = 50;
    public ThreadHandler(UIThreadHandler UIThread) {
        this.UIThread=UIThread;

    }
    public void nonBlocking(){
        this.start();
        System.out.println("Called");
    }

    @Override
    public void run() {
        super.run();

        ArrayList<Tiles> listTiles = this.UIThread.getFragmentGame().getTileList();

        for(int i = 0; i< listTiles.size(); i++){
            Tiles currTile = listTiles.get(i);

            currTile.setY(currTile.getY() + FRAME_CHANGE_RATE);
        }

        this.UIThread.setMove(listTiles);
        try{
            Thread.sleep(100);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
