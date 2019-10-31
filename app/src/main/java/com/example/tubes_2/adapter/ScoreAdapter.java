package com.example.tubes_2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tubes_2.R;
import com.example.tubes_2.model.Score;
import com.example.tubes_2.presenter.ScorePresenter;

public class ScoreAdapter extends BaseAdapter {
    Context ctx;
    ScorePresenter store;

    public ScoreAdapter(ScorePresenter store, Context ctx) {
        this.store = store;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return this.store.getScoreList().get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(this.ctx).inflate(R.layout.highscoreitem_layout, viewGroup, false);

        TextView tv = view.findViewById(R.id.score_label);
        Score scoreAtPos = (Score)this.getItem(i);
        tv.setText(Integer.toString(scoreAtPos.getScore()));

        return view;
    }
}