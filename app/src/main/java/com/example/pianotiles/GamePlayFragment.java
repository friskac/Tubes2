package com.example.pianotiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.LayoutInflater;
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

public class GamePlayFragment extends Fragment implements View.OnClickListener {
    final static int PAINT_STROKE_SIZE = 10;
    private Song mockSong;
    private Tiles tile;
    private ArrayList<Tiles> listTIle;
    private UIThreadHandler uiHandler;

    private TextView tvScore;
    private ImageView ivCanvas;
    private Button btnStart;
    private LinearLayout llBtnStart;

    private Canvas gameCanvas;
    private Paint strokePaint;


    private boolean isCanvasInitiated;

    private ArrayList<Rect> rectInsideCanvas;

    private FragmentListener listener;

    public GamePlayFragment() {

    }
    public ArrayList<Tiles> getTileList(){ return this.listTIle;}


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

        this.tvScore = binding.tvScore;
        this.ivCanvas = binding.ivCanvas;

        this.llBtnStart = binding.llstart;
        this.btnStart = binding.btnStart;
        this.btnStart.setOnClickListener(this);

        this.rectInsideCanvas = new ArrayList<>();

        return view;
    }

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
           this.renderTiles(20,400);
           ThreadHandler thread = new ThreadHandler(this.uiHandler);
           thread.nonBlocking();
        }
    }


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

    public void fillTheList(){
        this.listTIle = new ArrayList<>();
        int width = this.ivCanvas.getWidth()/4;
        int height = this.ivCanvas.getHeight()/4;
        this.listTIle.add(new Tiles(20, 100, width, height));
    }

    public void resetCanvas() {
        // 4. Draw canvas background
        //Reference: https://stackoverflow.com/questions/2172523/draw-object-image-on-canvas
        Drawable backGroundPicture = ResourcesCompat.getDrawable(getResources(), R.drawable.bg4, null);
        Rect imageBounds = this.gameCanvas.getClipBounds();
        backGroundPicture.setBounds(imageBounds);
        backGroundPicture.draw(gameCanvas);

        // 5. force draw
        this.ivCanvas.invalidate();

        // 6. reset stroke width + color
        this.strokePaint.setStrokeWidth(PAINT_STROKE_SIZE);
        this.changeStrokeColor(R.color.black);
    }


    private void changeStrokeColor(int color) {
        //change stroke color using parameter (color resource id)
        int mColor = ResourcesCompat.getColor(getResources(), color, null);
        this.strokePaint.setColor(mColor);
    }

    public void renderTiles(int x, int y){
        int width = this.ivCanvas.getWidth()/4;
        int height = this.ivCanvas.getHeight()/5;
        this.tile = new Tiles(x,y,width,height);
        Drawable bg = this.getResources().getDrawable(R.drawable.ic_black_rectangle);

        int left = tile.getX();
        int right = tile.getX() + tile.getWidth();
        int bottom = tile.getY();
        int top = tile.getY() - tile.getHeight();
        bg.mutate().setBounds(left, top, right, bottom);
        bg.draw(this.gameCanvas);

        this.ivCanvas.invalidate();
    }

    public void renderTiles2(Tiles tile){
        int width = this.ivCanvas.getWidth()/4;
        int height = this.ivCanvas.getHeight()/4;

        Drawable bg = this.getResources().getDrawable(R.drawable.ic_black_rectangle);

        int left = tile.getX();
        int right = tile.getX() +width;
        int bottom = tile.getY();
        int top = tile.getY() - height;
        bg.mutate().setBounds(left, top, right, bottom);
        bg.draw(this.gameCanvas);

        this.ivCanvas.invalidate();
    }

}
