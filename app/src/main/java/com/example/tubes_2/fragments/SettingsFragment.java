package com.example.tubes_2.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.tubes_2.R;
import com.example.tubes_2.interfaces.UIActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends DialogFragment implements View.OnClickListener {
    RadioGroup difficulty, controller;
    Button play;

    UIActivity activity;
    static int START_GAME=1;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_init_game, container, false);

        this.difficulty = view.findViewById(R.id.difficulty);
        this.controller = view.findViewById(R.id.controller);
        this.play = view.findViewById(R.id.play_button);

        this.play.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof UIActivity) {
            this.activity = (UIActivity)context;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        int chosenDifficulty = -1;
        int chosenController = -1;

        if (id == this.play.getId()) {
            if(view.findViewById(this.difficulty.getCheckedRadioButtonId()) == view.findViewById(R.id.normal_difficulty)){
                chosenDifficulty = 0;
            }
            else{
                chosenDifficulty = 1;
            }
            if(view.findViewById(this.controller.getCheckedRadioButtonId()) == view.findViewById(R.id.joystick)){
                chosenController = 0;
            } else chosenController = 1;
            this.activity.startGame(chosenDifficulty,chosenController);
        }
    }

    public static SettingsFragment newInstance(){
        return new SettingsFragment();
    }
}
