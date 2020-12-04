package com.example.pianotiles;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.pianotiles.databinding.FragmentDialogBinding;

public class PopupScoreFragment extends DialogFragment implements View.OnClickListener {
    FragmentListener listener;
    Presenter presenter;
    private TextView tvFinalScore;
    private Button btnPlayAgain;
    private Button btnExitToMenu;

    public PopupScoreFragment(){}

    public static PopupScoreFragment newInstance(Presenter presenter, Bundle args) {
        PopupScoreFragment fragment = new PopupScoreFragment();
        fragment.presenter = presenter;
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentDialogBinding binding = FragmentDialogBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        this.tvFinalScore = binding.tvFinalscore;
        this.btnPlayAgain = binding.btnPlayagain;
        this.btnPlayAgain.setOnClickListener(this);
        this.btnExitToMenu = binding.btnExitondialog;
        this.btnExitToMenu.setOnClickListener(this);

        if(getArguments() != null){
            int highScore = getArguments().getInt("highscore");
            this.tvFinalScore.setText(highScore+"");
            Log.d("debug popup", "presenter is "+this.presenter);
            if(this.presenter != null){
                this.presenter.getPreference().insertNewHighScore(highScore);
                this.listener.updateScore();
            }
        }

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

    @Override
    public void onClick(View v) {
        if(v == this.btnPlayAgain){
            this.listener.changePage(FragmentType.FRAGMENT_GAME_PLAY, true, null);
            dismiss();
        }else if(v == this.btnExitToMenu){
            this.listener.changePage(FragmentType.FRAGMENT_LOBBY, true, null);
            dismiss();
        }
    }
}
