package com.example.tubes_2.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tubes_2.R;
import com.example.tubes_2.interfaces.UIActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoringFragment extends DialogFragment implements View.OnClickListener {
    TextView score, penalty, mult, total;
    Button ext;
    UIActivity activity;

    public ScoringFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scoring, container, false);

        this.score = view.findViewById(R.id.total_score);
        this.penalty = view.findViewById(R.id.time_penalty);
        this.mult = view.findViewById(R.id.muiltiplier);
        this.total = view.findViewById(R.id.total_score);
        this.ext = view.findViewById(R.id.exit_scoring);

        this.score.setText(this.getArguments().getString("score"));
        this.penalty.setText(this.getArguments().getString("penalty"));
        this.mult.setText(this.getArguments().getString("mult"));
        this.total.setText(this.getArguments().getString("total"));

        this.ext.setOnClickListener(this);

        this.setCancelable(false);

        return view;
    }

    public static ScoringFragment newInstance(String score, String penalty, String mult, String total) {
        ScoringFragment fragment = new ScoringFragment();
        Bundle args = new Bundle();
        args.putString("score", score);
        args.putString("penalty", penalty);
        args.putString("mult", mult);
        args.putString("total", total);

        fragment.setArguments(args);

        return fragment;
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

        if (id == this.ext.getId()) {
            this.activity.updateScore(Integer.parseInt(this.total.getText().toString()));
            this.dismiss();
        }
    }
}
