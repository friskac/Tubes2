package com.example.pianotiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.constraintlayout.solver.widgets.Rectangle;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.pianotiles.databinding.FragmentGameplayBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static android.hardware.SensorManager.SENSOR_DELAY_GAME;

public class GamePlayFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, SensorEventListener {

    final static int MAX_TILES_IN_LIST = 8;

     final static int TAP_INCREMENT_TYPE = 0;
     final static int SENSOR_INCREMENT_TYPE = 1;
     final static int TAP_SCORE_INCREMENT = 1;
     final static int SENSOR_SCORE_INCREMENT = 25;

    public static final float VALUE_DRIFT = 0.05f;
    public static final float ACCURACY_DRIFT = 0.15f;

    //Model and lists
    private ArrayList<Tiles> listTile;
    private ArrayList<Tiles> fullTileList;
    private UIThreadHandler uiHandler;
    private ThreadHandler threadHandler;
    private Presenter presenter;
    private int scoreIncrement;
    private SensorBar sensorBar;

    //View related attributes
    private PopupScoreFragment popupScoreFragment;
    private TextView tvScore;
    private TextView tvScoreIncrement;
    private ImageView ivCanvas;
    private Button btnStart;
    private LinearLayout llBtnStart;
    private int currScore;
    private Context context;

    //Attributes for Canvas
    private Canvas gameCanvas;
    private Paint strokePaint;
    private boolean isCanvasInitiated;


    //TouchListener related attributes
    private GestureDetector mDetector;
    private MotionEvent.PointerCoords pointerCoords;


    //Attributes for Sensor
    private SensorManager mSensorManager;
    private float[] accelerometerReadings;
    private float[] magnetometerReadings;

    private Sensor accelerometer;
    private Sensor magnetometer;

    //Thread related attributes
    //Reference: https://stackoverflow.com/questions/3392139/thread-synchronization-java
    private ReentrantLock lock;
    private Condition condition;

    private FragmentListener listener;

    public GamePlayFragment() {
    }

    public ArrayList<Tiles> getTileList() {
        return this.listTile;
    }


    public static GamePlayFragment newInstance(Context context, Presenter presenter, Bundle args) {
        GamePlayFragment fragment = new GamePlayFragment();
        fragment.presenter = presenter;
        fragment.context = context;
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentGameplayBinding binding = FragmentGameplayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        this.currScore = 0;

        this.tvScore = binding.tvScore;
        this.ivCanvas = binding.ivCanvas;
        this.tvScoreIncrement = binding.tvScoreIncrement;

        this.llBtnStart = binding.llstart;
        this.btnStart = binding.btnStart;
        this.btnStart.setOnClickListener(this);
        this.mDetector = new GestureDetector(new MyDetector());
        this.ivCanvas.setOnTouchListener(this);

        this.listTile = new ArrayList<>();
        this.pointerCoords = null;

        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();

        this.uiHandler = new UIThreadHandler(this);

        this.mSensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
        if (this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            this.accelerometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (this.mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            this.magnetometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        this.accelerometerReadings = new float[3];
        this.magnetometerReadings = new float[3];

        this.scoreIncrement = TAP_SCORE_INCREMENT;

        return view;
    }

    /**
     * @param context
     */
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            this.listener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentListener");
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == this.btnStart.getId()) {
            this.initiateCanvas();
            this.btnStart.setVisibility(View.GONE);
            this.llBtnStart.setVisibility(View.GONE);
            this.fillTheList();
            threadHandler = new ThreadHandler(this.uiHandler, this.lock, this.ivCanvas.getHeight());
            threadHandler.nonBlocking();
        }
    }

    /**
     * Implementasi onTouch listener untuk ImageView yang digunakan dalam permainan
     * Alasan dipisah ada yang pake MyDetector sama da yang ga pake:
     * - MyDetector ga bisa deteksi keyRelease, jadi manual lewat switch case ACTION_POINTER_UP sama ACTION_UP
     * - MyDetector kepake buat motion kaya onScroll sama onFling kalo mau dilanjutin implementasi multitouch si tile
     *
     * @param v     view tempat touch event terjadi
     * @param event jenis event pada view
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Touch event hanya akan dihandle jika start button sudah di tekan
        //Tanpa pengecekan kondisi ini akan terjadi exception
        if (isCanvasInitiated) {
            int action = event.getAction();
            int pointerIndex = event.getActionIndex();
            MotionEvent.PointerCoords pointer = new MotionEvent.PointerCoords();
            event.getPointerCoords(pointerIndex, pointer);
            switch (action & event.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_UP:
                    int color = ResourcesCompat.getColor(getResources(), R.color.gray_shade, null);
                    recolorTile(pointer, color, false);
                    return true;

                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    int color2 = ResourcesCompat.getColor(getResources(), R.color.blue, null);
                    recolorTile(pointer, color2, true);
                    return true;
                default:
                    return this.mDetector.onTouchEvent(event);
            }
        }
        return true;
    }


    public void showGameDialog() {
        Bundle args = new Bundle();
        args.putInt("highscore", this.currScore);
        this.popupScoreFragment = PopupScoreFragment.newInstance(this.presenter, args);

        //Reference: https://stackoverflow.com/questions/44018711/how-to-dialogfragment-disable-click-outside-on-android
        //Set dialog agar tidak hilang ketika dilakukan touch pada area diluar dialog
        this.popupScoreFragment.setCancelable(false);
        this.popupScoreFragment.show(getFragmentManager(), popupScoreFragment.getTag());

    }

    /**
     * Metode untuk menghentikan thread ketika dilakukan back press dari gameplay fragment ke lobby
     */
    public void stopOnHide() {
        //Reference:https://developer.android.com/reference/android/app/Fragment.html#isHidden()
        if (isHidden()) {
            //Cek status fragment hidden atau tidak
            this.threadHandler.setStopped(true);
        }
    }


    //##############################################################################################
    //CANVAS RELATED METHODS
    //##############################################################################################

    /**
     * Inisiasi canvas yang akan digunakan dalam permainan
     */
    private void initiateCanvas() {
        // 1. Create Bitmap
        Bitmap mBitmap = Bitmap.createBitmap(this.ivCanvas.getWidth(), this.ivCanvas.getHeight(), Bitmap.Config.ARGB_8888);
        // 2. Associate the bitmap to the ImageView.
        this.ivCanvas.setImageBitmap(mBitmap);
        // 3. Create a Canvas with the bitmap.
        this.gameCanvas = new Canvas(mBitmap);
        // new paint for stroke + style (Paint.Style.STROKE)
        this.strokePaint = new Paint();
        this.strokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //resetCanvas
        this.resetCanvas();
        this.isCanvasInitiated = true;

        //Isi list tiles dengan tile dummy
        this.fullTileList = MockSong.generateMockTiles(this.ivCanvas.getWidth(), this.ivCanvas.getHeight());

        //Buat model dari sensor bar;
        this.sensorBar = new SensorBar(this.ivCanvas.getWidth(), this.ivCanvas.getHeight());
    }


    /**
     * Menggambar ulang canvas menjadi background gambar tanpa tile di dalamnya
     */
    public void resetCanvas() {
        //Draw canvas background
        //Reference: https://stackoverflow.com/questions/2172523/draw-object-image-on-canvas
        Drawable backGroundPicture = ResourcesCompat.getDrawable(getResources(), R.drawable.bg4, null);
        Rect imageBounds = this.gameCanvas.getClipBounds();
        backGroundPicture.setBounds(imageBounds);
        backGroundPicture.draw(gameCanvas);
        int mColor = ResourcesCompat.getColor(getResources(), R.color.white, null);
        this.strokePaint.setColor(mColor);

        //Menggambar ulang garis di antara jalur tile
        int canvasHeight = ivCanvas.getHeight();
        int lineX = ivCanvas.getWidth() / 4;
        int lineHeight = canvasHeight;
        for (int i = 1; i < 4; i++) {
            gameCanvas.drawLine(lineX * i, 0, lineX * i, lineHeight, strokePaint);
        }

        //force draw
        this.ivCanvas.invalidate();
    }

    /**
     * Pointer yang digunakan untuk mengambil lokasi sentuhan pada kanvas saat ini
     * @return
     */
    public MotionEvent.PointerCoords getLastPointerCoords() {
        return pointerCoords;
    }

    /**
     * Mengisi list tiles dengan tiles dummy
     */
    public void fillTheList() {
        this.lock.lock();
        //melakukan lock agar ketika menambahkan tile baru tidak akan muncul exception
        //karena melakukan modifikasi pada array list tile
        try {
            Log.d("debug fill list", "List size : " + this.listTile.size());
            int width = this.ivCanvas.getWidth() / 4;
            int height = this.ivCanvas.getHeight() / 5;
            int spacing = this.ivCanvas.getHeight() / 20;
            int initialSpace = this.ivCanvas.getHeight();

            Tiles prevTile = null;
            if (this.listTile.size() > 0) {
                prevTile = this.listTile.get(this.listTile.size() - 1);
            }

            while (this.listTile.size() <= MAX_TILES_IN_LIST && fullTileList.size() > 0) {
                Tiles currTile;
                if (prevTile == null) {
                    //Mengambil tile paling depan dari fullTileList untuk dimasukkan ke listTile
                    currTile = fullTileList.remove(0);
                } else {
                    //Mengambil tile paling depan dari fullTileList untuk dimasukkan ke bagian akhir listTile
                    // dengan tetap memperhatikan tiles paling akhir saat ini di listTile
                    currTile = fullTileList.remove(0);
                    int barIdx = currTile.getIndex();
                    int x = currTile.left();
                    int y = prevTile.top() - spacing;
                    int tileHeight = currTile.getHeight();

                    if (tileHeight < 1) {
                        tileHeight = height;
                    }
                    int tileWidth = width;
                    currTile = new Tiles(barIdx, x, y, tileWidth, tileHeight);
                }

//                Log.d("debug mock tile:", currTile.toString());
                this.listTile.add(currTile);
                prevTile = currTile;
            }
        } finally {
            //melakukan unlock agar thread lain mendapatkan akses ke array list tile
            lock.unlock();
        }

    }

    /**
     * Method untuk render ulang tiles yang ada di list
     */
    public void renderTiles() {
        //Reset ulang ImageView jadi background kosong
        this.resetCanvas();

        this.lock.lock();
        //melakukan lock agar ketika memproses tile tidak akan muncul exception
        //karena melakukan modifikasi pada array list tile
        try {
            //untuk setiap tile dibuat drawablenya
            for (int i = 0; i < this.listTile.size(); i++) {
                Tiles tile = listTile.get(i);

                Drawable bg = this.getResources().getDrawable(R.drawable.ic_black_rectangle);
                //set color berdasarkan status dari tile
                bg.mutate().setTint(tile.color);

                int left = tile.left();
                int right = tile.right();
                int bottom = tile.bottom();
                int top = tile.top();


                bg.mutate().setBounds(left, top, right, bottom);
                bg.draw(this.gameCanvas);
            }

        } finally {
            //melakukan unlock agar thread lain mendapatkan akses ke array list tile
            lock.unlock();
        }

        //Gambar ulang garis sensor bar
        int mColor = ResourcesCompat.getColor(getResources(), R.color.white, null);
        this.strokePaint.setColor(mColor);
        this.gameCanvas.drawRect(new Rect(sensorBar.left, sensorBar.top, sensorBar.right, sensorBar.bottom), this.strokePaint);

        //Gambar ulang lingkaran terluar sensor bar
         mColor = ResourcesCompat.getColor(getResources(), R.color.plum, null);
        this.strokePaint.setColor(mColor);
        this.gameCanvas.drawCircle(sensorBar.cx, sensorBar.cy, sensorBar.radius, strokePaint);

        //Gambar ulang lingkaran dalam sensor bar
        mColor = ResourcesCompat.getColor(getResources(), R.color.blue, null);
        this.strokePaint.setColor(mColor);
        this.gameCanvas.drawCircle(sensorBar.cxMiniCircle, sensorBar.cyMiniCircle, sensorBar.radMiniCircle, strokePaint);

        this.ivCanvas.invalidate();
    }


    /**
     * Menggambar ulang tile yang ada di tileList ke canvas berdasarkan hasil update dari thread
     *
     * @param coords  koordinat touch event
     * @param color   warna berdasarkan event touch
     * @param pressed state bernilai true untuk event ACTION_POINTER_DOWN atau ACTION_DOWN dan bernilai false untuk event ACTION_POINTER_UP atau ACTION_UP
     */
    public void recolorTile(MotionEvent.PointerCoords coords, int color, boolean pressed) {
        Tiles prevTile = null;

        this.lock.lock();
        //melakukan lock agar ketika memproses tile tidak akan muncul exception
        //karena melakukan modifikasi pada array list tile
        try {
            Iterator<Tiles> iterator = this.listTile.iterator();
            while (this.listTile.size() > 0 && iterator.hasNext()) {
                Tiles currTile = iterator.next();

                //Cek tile sebelumnya sudah pernah disentuh atau belum, jika belum pernah, tile yang saat ini disentuh tidak akan diubah statusnya
                if (prevTile != null) {
                    if (pressed && !prevTile.isPressed()) {
                        continue;
                    }
                }

                if (pressed) {
                    //Merupakan event ACTION_POINTER_DOWN atau ACTION_DOWN
                    Log.d("debug press", "pressed" + coords.toString());

                    this.pointerCoords = coords;

                    //Cek koordinat sentuh berada di dalam area tiles
                    if ((currTile.getX() <= coords.x && (currTile.getX() + currTile.getWidth()) > coords.x) && (currTile.getY() >= coords.y && (currTile.getY() - currTile.getHeight()) < coords.y)) {

                        //set warna dari tile
                        currTile.setColor(color);
                        //set state tiles pernah disentuh
                        currTile.setPressed(true);

                    }
                } else {
                    //Merupakan event ACTION_POINTER_UP atau ACTION_UP
                    Log.d("debug press", "released" + coords.toString());
                    this.pointerCoords = null;

                    if (currTile.isPressed() //Cek tiles pernah di tekan atau tidak. Ada kasus dimana event tekan di luar area tiles, namun release di dalam area tiles
                            && (currTile.left() <= coords.x) //Cek koordinat x dari event sentuh berada di sebelah kanan dari tiles
                            && (currTile.right() >= coords.x) //Cek koordinat x dari event sentuh berada di sebelah kiri dari tiles
                            && (currTile.bottom() >= coords.y)//Cek koordinat bottom dari tile harus lebih besar dari nilai koordinat y dari event sentuh
                        //Koordinat top dapat diabaikan dan hanya dilakukan pengecekan koordinat bottom saja, karena ada kasus touch tepat pada tiles, namun release terjadi di luar area tiles
                    ) {
                        //set warna dari tile
                        currTile.setColor(color);
                        //set state tiles sudah tidak disentuh lagi (released)
                        currTile.setReleased(true);

                    }
                    this.tvScoreIncrement.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.scale_out_in));
                    this.tvScoreIncrement.setText("+ " + this.scoreIncrement);
                    this.tvScoreIncrement.clearAnimation();
                }

                //Cek kondisi tiles sedang disentuh dan belum pernah dilepas.
                //Jika sudah pernah disentuh dan sudah pernah dilepas, maka skor tidak akan ditambahkan
                if (currTile.isPressed() && !currTile.isReleased()) {
                    increaseScore(TAP_INCREMENT_TYPE);
                }

                //assign pointer ke tile saat ini untuk digunakan oleh tiles selanjutnya
                prevTile = currTile;
            }
        } finally {
            //melakukan unlock agar thread lain mendapatkan akses ke array list tile
            lock.unlock();
        }


    }

    public void increaseScore(int type) {
        //Cek kondisi tiles sedang disentuh dan belum pernah dilepas.
        //Jika sudah pernah disentuh dan sudah pernah dilepas, maka skor tidak akan ditambahkan

        //Menambahkan skor berdasarkan jenis interaksi dengan tile
        if(type == SENSOR_INCREMENT_TYPE){
            this.scoreIncrement = SENSOR_SCORE_INCREMENT;
        }else if(type == TAP_INCREMENT_TYPE){
            this.scoreIncrement = TAP_SCORE_INCREMENT;
        }

        this.currScore += this.scoreIncrement;
        this.tvScore.setText(this.currScore + "");
        this.tvScoreIncrement.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.scale_out_in));
        this.tvScoreIncrement.setText("+ " + this.scoreIncrement);
        this.tvScoreIncrement.clearAnimation();
    }

    //##############################################################################################
    //SENSOR RELATED METHODS
    //##############################################################################################


    public SensorBar getSensorBar() {
        return sensorBar;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.d("debug sensor", "Sensor change");
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                this.accelerometerReadings = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                this.magnetometerReadings = event.values.clone();
                break;
        }

        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReadings, magnetometerReadings);

        float[] orientationValues = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientationValues);


        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        if (Math.abs(azimuth) < VALUE_DRIFT) {
            azimuth = 0;
        }
        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0;
        }

        if (isCanvasInitiated) {
            if (Math.abs(roll) > ACCURACY_DRIFT) {
                float ceiledRoll =  (float)Math.ceil((double)(roll));
                if(ceiledRoll <= 0 ){
                    ceiledRoll = 1;
                }
                if (roll < 0) {
                    sensorBar.modifyCX((float)(sensorBar.CIRCLE_MOVEMENT_SPEED*-ceiledRoll));
                } else if (roll > 0) {
                    sensorBar.modifyCX((float)(sensorBar.CIRCLE_MOVEMENT_SPEED*ceiledRoll));
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.accelerometer != null) {
            this.mSensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (this.magnetometer != null) {
            this.mSensorManager.registerListener(this, this.magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mSensorManager.unregisterListener(this);
    }


    private class MyDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (e1 != null) {
                int pointerIndex = e1.getActionIndex();
                int pointerId = e1.getPointerId(pointerIndex);
                Log.d("debug scroll", "Pointer ID: " + pointerId);
                Log.d("debug scroll", String.format("Scroll start [x] : %.3f, [y] : %.3f", e1.getX(), e1.getY()));
            } else {

                int pointerIndex = e2.getActionIndex();
                int pointerId = e2.getPointerId(pointerIndex);
                Log.d("debug scroll", "Pointer ID: " + pointerId);
                Log.d("debug scroll", String.format("Scroll e2 start [x] : %.3f, [y] : %.3f", e2.getX(), e2.getY()));
            }
            return false;
        }

        public void onLongPress(MotionEvent e) {
            Log.d("debug", String.format("long press [ x ]: %.3f, [ y ]: %.3f", e.getX(), e.getY()));
        }
    }
}
