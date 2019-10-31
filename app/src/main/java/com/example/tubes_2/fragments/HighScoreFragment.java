package com.example.tubes_2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tubes_2.R;
import com.example.tubes_2.interfaces.UIActivity;

public class HighScoreFragment extends Fragment {
    UIActivity activity;

    public static HighScoreFragment newInstance(UIActivity activity) {
        Bundle args = new Bundle();
        HighScoreFragment hsFragment = new HighScoreFragment();
        hsFragment.setArguments(args);
        hsFragment.setActivity(activity);
        return hsFragment;
    }

    private void setActivity(UIActivity activity) {this.activity=activity;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_highscore,container,false);
        return view;
    }

}
