package com.example.pianotiles;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.view.View;
import android.view.ViewGroup;

public class LobbyFragment extends Fragment {
    protected Button btnPlay;
    protected Button btnSetting;
    protected Button btnHS;
    protected Button btnExit;

    public LobbyFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lobby,container,false);
        this.btnPlay = view.findViewById(R.id.btn_play);
        this.btnHS = view.findViewById(R.id.btn_score);
        this.btnSetting = view.findViewById(R.id.btn_setting);
        this.btnExit = view.findViewById(R.id.btn_exit);

        this.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        this.btnHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        this.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        this.btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

}
