package com.example.pianotiles;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadHandler extends java.lang.Thread {
    protected UIThreadHandler UIThread;
    protected int FRAME_CHANGE_RATE = 75;
    protected int canvasHeight;
    protected boolean isStopped;
    protected ReentrantLock lock;

    public ThreadHandler(UIThreadHandler UIThread, ReentrantLock lock, int canvasHeight) {
        this.UIThread = UIThread;
        this.canvasHeight = canvasHeight;
        this.isStopped = false;
        this.lock = lock;
    }

    public void nonBlocking() {
        this.start();
        System.out.println("Called");
    }

    public void setStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

    @Override
    public void run() {
        super.run();

        boolean noMissedTile = true;
        boolean gameFinished = false;

        while (noMissedTile && !gameFinished && !isStopped) {
            this.lock.lock();
            //melakukan lock agar ketika memproses tile tidak akan muncul exception
            //karena melakukan modifikasi pada array list tile
            try {
                ArrayList<Tiles> listTiles = this.UIThread.getFragmentGame().getTileList();
                ArrayList<Tiles> toBeDelete = new ArrayList<>();
                if (listTiles.isEmpty()) {
                    gameFinished = true;
                    continue;
                }


                Log.d("debug thread", listTiles.size() + "");

                for (int i = 0; i < listTiles.size(); i++) {
                    Tiles currTile = listTiles.get(i);
                    currTile.setY(currTile.getY() + FRAME_CHANGE_RATE);


                    if ((currTile.getY() > this.canvasHeight) && !currTile.isPressed) {
                        //koordinat bottom tiles berada di luar canvas dan belum pernah ditekan
                        //Game akan berhenti
                        int color = Color.argb(255, 217, 28, 37);
                        currTile.setColor(color);
                        noMissedTile = false;
                        //Melakukan break untuk memunculkan dialog
                        break;
                    } else if ((currTile.getY() > this.canvasHeight) && currTile.isPressed && !currTile.isReleased()) {
                        //Tile sudah memiliki state pressed bernilai true dan keluar dari canvas, tapi warnanya masih menggunakan warna status pressed
                        int color = Color.argb((int) (255 * 0.75), 147, 157, 165);

                        //set warna dari tile
                        currTile.setColor(color);

                        //set state released dari tile
                        //Ada kemungkinan user melakukan fling sampai tiles keluar dan state release dari tiles tidak pernah diubah
                        currTile.setReleased(true);
                    }

                    //Tiles sudah keluar dari canvas, dapat dimasukkan ke list tiles yang akan dihapus untuk memperkecil ukuran tiles yang masih harus diproses
                    if ((currTile.top() > this.canvasHeight) && currTile.isPressed) {
                        toBeDelete.add(currTile);
                    }
                }

                //Menghapus tiles yang sudah keluar dari area canvas
                for (int i = 0; i < toBeDelete.size(); i++) {
                    listTiles.remove(toBeDelete.get(i));
                }
            } finally {
                //melakukan unlock agar thread lain mendapatkan akses ke array list tile
                lock.unlock();
            }
            //Gambar ulang semua tile di ImageView
            this.UIThread.setMove();
            this.UIThread.reFillList();

            //Cek visibilitas fragment gameplay
            this.UIThread.checkFragmentVisibility();

            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Memunculkan dialog apabila game berakhir atau ada tiles yang keluar canas tanpa pernah disentuh
        if (!noMissedTile || gameFinished) {
            //toggle dialog game selesai lewat UIThreadHandler
            UIThread.toggleDialog();
        }

    }
}
