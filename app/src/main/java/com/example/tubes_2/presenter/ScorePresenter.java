package com.example.tubes_2.presenter;

import com.example.tubes_2.model.Score;

import java.util.ArrayList;
import java.util.List;

public class ScorePresenter {
    List<Score> scoreList;

    public ScorePresenter() {
        this.scoreList = new ArrayList<>();
    }

    public void addScores(Score score) {
        this.scoreList.add(score);
    }

    public List<Score> getScoreList() {
        return scoreList;
    }
}
