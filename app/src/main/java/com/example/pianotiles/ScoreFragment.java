package com.example.pianotiles;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class ScoreFragment extends Fragment{
    private ListView lvScore;
    private AdapterHighScore adapter;
    private Presenter presenter;
    private static ScoreFragment scoreFragment;

    public static ScoreFragment newInstance(Presenter presenter, AdapterHighScore adapter){
        scoreFragment = new ScoreFragment();
        scoreFragment.adapter = adapter;
        scoreFragment.presenter = presenter;
        return scoreFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_highscore, container, false);

        this.lvScore = view.findViewById(R.id.list_score);
        this.lvScore.setAdapter(scoreFragment.adapter);
        return view;
    }

    public void updateScore(){
        this.adapter.updateScore();
    }
}
