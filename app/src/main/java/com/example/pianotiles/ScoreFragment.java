package com.example.pianotiles;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class ScoreFragment extends Fragment{
    protected SharedPreferences sp;
    protected GamePlayFragment gfp;
    protected int score;
    protected int highScore;
    protected TextView tvScore;

    public ScoreFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_highscore, container, false);

        this.tvScore = view.findViewById(R.id.list_score);
        return view;
    }

//    private int Display(){
//        SharedPreferences preferences =
//                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        int myInt = preferences.getInt("myHighScore", -1);
//        username.setText("High Score : " + myInt);
//    }


}
