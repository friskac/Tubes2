package com.example.pianotiles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.pianotiles.databinding.FragmentDialogBinding;

public class PopupScoreFragment extends DialogFragment {
    private TextView tvFinalScore;
    private Button btnPlayAgain;
    private Button btnExitToMenu;

    public PopupScoreFragment(){}

    public static PopupScoreFragment newInstance(Bundle args) {
        PopupScoreFragment fragment = new PopupScoreFragment();
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
        this.btnExitToMenu = binding.btnExitondialog;

        return view;
    }
}
