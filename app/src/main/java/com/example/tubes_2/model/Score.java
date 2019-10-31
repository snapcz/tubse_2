package com.example.tubes_2.model;

public class Score {
    int order, value;

    public Score(int order, int value) {
        this.order = order;
        this.value = value;
    }

    public int getOrder() {
        return this.order;
    }

    public int getScore() {
        return this.value;
    }
}
