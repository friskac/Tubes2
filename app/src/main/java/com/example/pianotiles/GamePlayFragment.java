package com.example.pianotiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.solver.widgets.Rectangle;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.pianotiles.databinding.FragmentGameplayBinding;

import java.util.ArrayList;
import java.util.Iterator;

public class GamePlayFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    final static int PAINT_STROKE_SIZE = 10;
    private Song mockSong;
    private Tiles tile;
    private PopupScoreFragment popupScoreFragment;
    private ArrayList<Tiles> listTile;
    private UIThreadHandler uiHandler;

    private TextView tvScore;
    private ImageView ivCanvas;
    private Button btnStart;
    private LinearLayout llBtnStart;

    private Canvas gameCanvas;
    private Paint strokePaint;

    private GestureDetector mDetector;

    private boolean isCanvasInitiated;
    private int currScore;

    private FragmentListener listener;

    public GamePlayFragment() {

    }

    public ArrayList<Tiles> getTileList() {
        return this.listTile;
    }


    public static GamePlayFragment newInstance(Bundle args) {
        GamePlayFragment fragment = new GamePlayFragment();
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

        this.llBtnStart = binding.llstart;
        this.btnStart = binding.btnStart;
        this.btnStart.setOnClickListener(this);

        this.mDetector = new GestureDetector(new MyDetector());
        this.ivCanvas.setOnTouchListener(this);

        this.uiHandler = new UIThreadHandler(this);

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
            ThreadHandler thread = new ThreadHandler(this.uiHandler, this.ivCanvas.getHeight());
            thread.nonBlocking();
        }
    }

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
    }

    /**
     * Mengisi list tiles dengan tiles dummy
     */
    public void fillTheList() {
        this.listTile = new ArrayList<>();
        int width = this.ivCanvas.getWidth() / 4;
        int height = this.ivCanvas.getHeight() / 5;
        int spacing = this.ivCanvas.getHeight() / 20;
        int initialSpace = this.ivCanvas.getHeight();
        Log.d("debug", "height : " + height);
        Log.d("debug", "width : " + width);

        Song mockSong = MockSong.getMockSong();
        Iterator<Note> iterator = mockSong.getNotes().iterator();
        Tiles prevTile = null;
        while (iterator.hasNext()) {
            Tiles currTile;
            if (prevTile == null) {
                Note currNote = iterator.next();
                int barIdx = currNote.getxBarIndex();
                int x = (barIdx * width);

//                int y = (int) (Math.floor(currNote.getStartTime() / 100000) - initialSpace);
//                int tileHeight = (int) ((Math.floor(currNote.getNoteDuration() / 100000)) * height);

                int y = 0 - initialSpace;
                int tileHeight = height;

                if (tileHeight < 1) {
                    tileHeight = height;
                }
                int tileWidth = width;
                currTile = new Tiles(x, y, tileWidth, tileHeight);
            } else {
                Note currNote = iterator.next();
                int barIdx = currNote.getxBarIndex();
                int x = (barIdx * width);
//                int y = (int) (Math.floor(currNote.getStartTime() / 100000))-prevTile.top()-spacing;
                int y = prevTile.top() - spacing;
//                int tileHeight = (int) ((Math.floor(currNote.getNoteDuration() / 100000)) * height);
                int tileHeight =  height;

                if (tileHeight < 1) {
                    tileHeight = height;
                }
                int tileWidth = width;
                currTile = new Tiles(x, y, tileWidth, tileHeight);
            }

            this.listTile.add(currTile);
            prevTile = currTile;
        }

        Log.d("debug fill list", "List size : " + this.listTile.size());
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

        //force draw
        this.ivCanvas.invalidate();
    }

    public void renderTiles(int x, int y) {
        int width = this.ivCanvas.getWidth() / 4;
        int height = this.ivCanvas.getHeight() / 4;
        this.tile = new Tiles(x, y, width, height);
        Drawable bg = this.getResources().getDrawable(R.drawable.ic_black_rectangle);

        int left = tile.getX();
        int right = tile.getX() + tile.getWidth();
        int bottom = tile.getY();
        int top = tile.getY() - tile.getHeight();
        bg.mutate().setBounds(left, top, right, bottom);
        bg.draw(this.gameCanvas);

        this.ivCanvas.invalidate();
    }

    /**
     * Method untuk render ulang tiles yang ada di list
     */
    public void renderTiles() {
        //Reset ulang ImageView jadi background kosong
        this.resetCanvas();

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

        this.ivCanvas.invalidate();
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
        int action = event.getAction();
        int pointerIndex = event.getActionIndex();
        MotionEvent.PointerCoords pointer = new MotionEvent.PointerCoords();
        event.getPointerCoords(pointerIndex, pointer);
        switch (action & event.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                int color = ResourcesCompat.getColor(getResources(), R.color.red,null);
                recolorTile(pointer,color,false);
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                int color2 = Color.argb(255, 77, 128, 205);
                recolorTile(pointer,color2,true);
            default:
                return this.mDetector.onTouchEvent(event);
        }
    }

    /**
     * Method buat process si motion event untuk action event tertentu
     *
     * @param debugType
     * @param event
     * @return
     */
//    public boolean processMotionEvent(String debugType, MotionEvent event) {
//        int pointerSize = event.getPointerCount();
//        Log.d(debugType, "Pointer size: " + pointerSize);
//        int pointerId;
//        int pointerIndex;
//        if (pointerSize > 0) {
//            pointerIndex = event.getActionIndex();
//            pointerId = event.getPointerId(pointerIndex);
//            Log.d(debugType, "Pointer ID: " + pointerId);
//            MotionEvent.PointerCoords pointer = new MotionEvent.PointerCoords();
//            event.getPointerCoords(pointerIndex, pointer);
//            Log.d(debugType, String.format("release [x] : %.3f, [y] : %.3f", pointer.x, pointer.y));
//            if (isCanvasInitiated) {
//                if (debugType.equals("touch")) {
//                    int color = Color.argb(255, 77, 128, 205);
//                    recolorTile(pointerId, pointer, color, true, false);
//                } else if (debugType.equals("release")) {
//                    int color = Color.argb((int) (255 * 0.75), 147, 157, 165);
//                    recolorTile(pointerId, pointer, color, true, true);
//                }
//            }
//        }
//
//        return true;
//    }

    /**
     * Menggambar ulang tile yang ada di tileList ke canvas berdasarkan hasil update dari thread
     *
     * @param pointerId pointer untuk touch event (kalo mau ada implementasi multitouch)
     * @param coords    koordinat touch event
     * @param color     warna berdasarkan event touch
     * @param pressed   state bernilai true untuk event ACTION_POINTER_DOWN atau ACTION_DOWN
     * @param released  state bernilai true untuk event ACTION_POINTER_UP atau ACTION_UP
     */
    public void recolorTile(MotionEvent.PointerCoords coords, int color, boolean pressed) {
        Iterator<Tiles> iterator = this.listTile.iterator();

        while (iterator.hasNext()) {
            Tiles currTile = iterator.next();

            //Cek jika koordinat touch event berada di dalam area tile
            if ((currTile.getX() <= coords.x && (currTile.getX() + currTile.getWidth()) > coords.x) && (currTile.getY() >= coords.y && (currTile.getY() - currTile.getHeight()) < coords.y)) {

                //set warna dari tile
                currTile.setColor(color);
                if (pressed){
                    currTile.setPressed(true);
                }
                else {
                    currTile.setReleased(true);
                }
                if (!pressed && currTile.isPressed() && !currTile.isReleased()){
                    this.currScore += 10;
                    this.tvScore.setText(this.currScore);
                }

            }
        }
    }

    public void showGameDialog(){
        this.popupScoreFragment = new PopupScoreFragment();
        this.popupScoreFragment.show(getFragmentManager(), popupScoreFragment.getTag());
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
