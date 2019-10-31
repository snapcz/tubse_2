package com.example.tubes_2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import com.example.tubes_2.R;
import com.example.tubes_2.adapter.ScoreAdapter;
import com.example.tubes_2.model.Score;
import com.example.tubes_2.presenter.ScorePresenter;

import java.util.List;

public class HighScoreFragment extends DialogFragment implements View.OnClickListener {
    ListView lvScore;
    List<Score> scores;

    Button exit;

    public HighScoreFragment(List<Score> scores) {
        this.scores = scores;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_highscore, container,false);

        this.lvScore = view.findViewById(R.id.high_score_list);
        this.exit = view.findViewById(R.id.exit_high_scores);
        ScorePresenter presenter = new ScorePresenter();

        for (Score x: this.scores) {
            presenter.addScores(x);
        }

        ScoreAdapter adapter = new ScoreAdapter(presenter, this.getContext());

        this.lvScore.setAdapter(adapter);
        this.exit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == this.exit.getId()) {
            this.dismiss();
        }
    }
}
