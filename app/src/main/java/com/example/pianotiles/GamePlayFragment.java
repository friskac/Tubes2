package com.example.pianotiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.pianotiles.databinding.FragmentGameplayBinding;

import java.util.ArrayList;

public class GamePlayFragment extends Fragment implements View.OnClickListener {
    final static int PAINT_STROKE_SIZE = 10;
    private Song mockSong;

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

        this.llBtnStart = binding.llStart;
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
        if (view == this.btnStart) {
           this.initiateCanvas();
           this.btnStart.setVisibility(View.GONE);
           this.llBtnStart.setVisibility(View.GONE);
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


    public void resetCanvas() {
        // 4. Draw canvas background
        int mColorWhite = ResourcesCompat.getColor(getResources(), R.color.white, null);
        this.gameCanvas.drawColor(mColorWhite);

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

}
