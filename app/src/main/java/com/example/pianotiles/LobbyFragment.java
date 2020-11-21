package com.example.pianotiles;

import androidx.fragment.app.Fragment;

import android.content.Context;
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
    protected FragmentType fragmentType;
    private FragmentListener listener;

    public static LobbyFragment newInstance(){
        LobbyFragment lf = new LobbyFragment();
        return lf;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_lobby,container,false);
        this.btnPlay = view.findViewById(R.id.btn_play);
        this.btnHS = view.findViewById(R.id.btn_score);
        this.btnSetting = view.findViewById(R.id.btn_setting);
        this.btnExit = view.findViewById(R.id.btn_exit);

        this.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.changePage(FragmentType.FRAGMENT_GAME_PLAY);
            }
        });

        this.btnHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.changePage(FragmentType.FRAGMENT_HIGH_SCORE);
            }
        });

        this.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.changePage(FragmentType.FRAGMENT_SETTING);
            }
        });

        this.btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.closeApplication();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            this.listener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentListener");
        }
    }

}
