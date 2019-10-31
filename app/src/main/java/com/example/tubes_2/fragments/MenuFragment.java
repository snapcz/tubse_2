package com.example.tubes_2.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.tubes_2.R;
import com.example.tubes_2.interfaces.UIActivity;
import com.example.tubes_2.model.Difficulty;

public class MenuFragment extends Fragment implements View.OnClickListener{
    Button startGame,highScore;
    final int START_GAME = 1;
    final int HIGH_SCORE = 2;
    UIActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        this.startGame = view.findViewById(R.id.new_game);
        this.highScore = view.findViewById(R.id.high_score);
        this.startGame.setOnClickListener(this);
        this.highScore.setOnClickListener(this);
        return view;
    }

    public void setActivity(UIActivity activity){this.activity = activity;}

    public static MenuFragment newInstance(UIActivity activity) {
        MenuFragment fragment = new MenuFragment();
        fragment.setActivity(activity);

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==this.startGame.getId()){
            this.activity.changePage(START_GAME);
        } else{
            this.activity.changePage(HIGH_SCORE);
        }
    }
}
