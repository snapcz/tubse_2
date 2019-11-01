package com.example.tubes_2.model;

public class Score implements Comparable<Score> {
    int order, value;

    public Score(int order, int value) {
        this.order = order;
        this.value = value;
    }

    public int getOrder() {
        return order;
    }

    public int getScore() {
        return this.value;
    }

    @Override
    public int compareTo(Score score) {
        if (this.value != score.value) {
            return score.value - this.value;
        }

        return this.order - score.order;
    }
}
