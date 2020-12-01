package com.example.pianotiles;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Random;

public class ThreadHandler extends java.lang.Thread {
    protected UIThreadHandler UIThread;
    protected int FRAME_CHANGE_RATE = 75;
    protected int canvasHeight;

    public ThreadHandler(UIThreadHandler UIThread, int canvasHeight) {
        this.UIThread = UIThread;
        this.canvasHeight = canvasHeight;

    }

    public void nonBlocking() {
        this.start();
        System.out.println("Called");
    }

    @Override
    public void run() {
        super.run();

        boolean noMissedTile = true;
        boolean gameFinished = false;

        while (noMissedTile && !gameFinished) {
            ArrayList<Tiles> listTiles = this.UIThread.getFragmentGame().getTileList();
            ArrayList<Tiles> toBeDelete = new ArrayList<>();
            if(listTiles.isEmpty()){
                gameFinished = true;
                continue;
            }

            for (int i = 0; i < listTiles.size(); i++) {
                Tiles currTile = listTiles.get(i);
                currTile.setY(currTile.getY() + FRAME_CHANGE_RATE);

                if ((currTile.getY() > this.canvasHeight) && !currTile.isPressed) {
                    int color = Color.argb(255, 217, 28, 37);
                    currTile.setColor(color);
                    noMissedTile = false;
                } else if ((currTile.getY() > this.canvasHeight) && currTile.isPressed && !currTile.isReleased()) {
                    //Tile sudah memiliki state pressed bernilai true dan keluar dari canvas, tapi warnanya masih menggunakan warna status pressed
                    int color = Color.argb((int) (255 * 0.75), 147, 157, 165);

                    //set warna dari tile
                    currTile.setColor(color);

                    //set state released dari tile
                    currTile.setReleased(true);
                }

                if ((currTile.top() > this.canvasHeight) && currTile.isPressed) {
                    toBeDelete.add(currTile);
                }

            }

            for (int i = 0; i < toBeDelete.size(); i++) {
                listTiles.remove(toBeDelete.get(i));
            }

            if (noMissedTile == false || gameFinished) {
                //toggle dialog game selesai lewat UIThreadHandler

            }

            //Gambar ulang semua tile di ImageView
            this.UIThread.setMove();
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
