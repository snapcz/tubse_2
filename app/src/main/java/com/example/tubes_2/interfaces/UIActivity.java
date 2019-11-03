package com.example.tubes_2.interfaces;

public interface UIActivity {
    void changePage(int code);
    void updateScore(int score);
    void showLoser();
    void startGame(int difficulty, int controller);
}
