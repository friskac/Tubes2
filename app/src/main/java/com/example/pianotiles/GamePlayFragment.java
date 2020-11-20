package com.example.pianotiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class GamePlayFragment extends Fragment {
    MockSong mockSong;

    public GamePlayFragment(){

    }


    public static GamePlayFragment newInstance(Bundle args) {
        GamePlayFragment fragment = new GamePlayFragment();
        fragment.setArguments(args);
        return fragment;
    }



}
