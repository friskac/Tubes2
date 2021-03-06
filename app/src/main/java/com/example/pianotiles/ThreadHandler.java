package com.example.pianotiles;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;
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

                SensorBar sensorBar = this.UIThread.getFragmentGame().getSensorBar();
                Iterator<Tiles> iterator = listTiles.iterator();
                int counter =0;
                while(iterator.hasNext()){
                    Tiles currTile = iterator.next();
                    Log.d("debug thread tile:", currTile.toString());

                    //Cek apakah posisi tile berada di bawah sensor
                    if (currTile.bottom() >= sensorBar.getCircleTop() //Nilai bottom dari tile sama dengan atau lebih besar dari nilai top dari lingkaran pada sensor bar
                            && (currTile.left() <= sensorBar.getCircleRight() //Nilai left dari tile sama dengan atau lebih kecil dari nilai left dari lingkaran pada sensor bar
                            && currTile.right() >= sensorBar.getCircleLeft()) //Nilai right dari tile sama dengan atau lebih dari nilai right dari lingkaran pada sensor bar
                            && !currTile.isPressed() //hanya diproses sensor jika tile belum pernah ditekan
                    ) {
                        //Ubah state tile jadi sudah pernah ditekan dan sudah dilepaskan
                        currTile.setPressed(true);
                        currTile.setReleased(true);
                        this.UIThread.increaseScore(GamePlayFragment.SENSOR_INCREMENT_TYPE);
                        Log.d("debug thread", "sensor is pressed i " +counter+" "+ currTile.isPressed());
                        Log.d("debug thread", "bttom:" + currTile.bottom());
                    }

                    if ((currTile.bottom() > this.canvasHeight) && !currTile.isPressed()) {
                        Log.d("debug thread", "is pressed i " +counter+" "+ currTile.isPressed());
                        Log.d("debug thread", "bttom:" + currTile.bottom());
                        //koordinat bottom tiles berada di luar canvas dan belum pernah ditekan
                        //Game akan berhenti
                        int color = Color.argb(255, 217, 28, 37);
                        currTile.setColor(color);
                        noMissedTile = false;
                        //Melakukan break untuk memunculkan dialog
                        break;
                    } else if (((currTile.bottom() > this.canvasHeight) && currTile.isPressed && !currTile.isReleased())
                            || (currTile.isPressed() && currTile.isReleased())) {
                        //Tile sudah memiliki state pressed bernilai true dan keluar dari canvas, tapi warnanya masih menggunakan warna status pressed
                        int color = Color.argb((int) (255 * 0.75), 147, 157, 165);

                        //set warna dari tile
                        currTile.setColor(color);

                        //set state released dari tile
                        //Ada kemungkinan user melakukan fling sampai tiles keluar dan state release dari tiles tidak pernah diubah
                        currTile.setReleased(true);
                    }

                    //Cek apakah pointer tekan dari onTouch listener merupakan object atau buka
                    //Jika bernilai bukan null, maka  event touch sedang terjadi dan event release belum terjadi
                    //jika bernilai null, maka event release sudah terjadi
                    if (currTile.isPressed() && !currTile.isReleased()) {
                        MotionEvent.PointerCoords coords = this.UIThread.getFragmentGame().getLastPointerCoords();
                        if (coords != null) {
                            if (currTile.isPressed() //Cek tiles pernah di tekan atau tidak. Ada kasus dimana event tekan di luar area tiles, namun release di dalam area tiles
                                    && (currTile.left() <= coords.x) //Cek koordinat x dari event sentuh berada di sebelah kanan dari tiles
                                    && (currTile.right() >= coords.x) //Cek koordinat x dari event sentuh berada di sebelah kiri dari tiles
                                    && (currTile.bottom() >= coords.y)//Cek koordinat bottom dari tile harus lebih besar dari nilai koordinat y dari event sentuh
                                //Koordinat top dapat diabaikan dan hanya dilakukan pengecekan koordinat bottom saja, karena ada kasus touch tepat pada tiles, namun release terjadi di luar area tiles
                            ) {
                                this.UIThread.increaseScore(GamePlayFragment.TAP_INCREMENT_TYPE);
                            }
                        }

                    }

                    //Tiles sudah keluar dari canvas, dapat dimasukkan ke list tiles yang akan dihapus untuk memperkecil ukuran tiles yang masih harus diproses
                    if ((currTile.top() > this.canvasHeight) && currTile.isPressed) {
                        toBeDelete.add(currTile);
                    }

                    //Di set di akhir agar tidak menyebabkan game keluar sebelum berhasil melakukan render ulang nilai bottom yang telah berubah
                    int newBottomValue = currTile.getY() + FRAME_CHANGE_RATE;
                    currTile.setY(newBottomValue);counter++;
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

            //Mengisi kembali list tile yang akan diprose
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
