package com.example.tubes_2.model.thread;

import com.example.tubes_2.presenter.GameStatus;

public class TimerThread extends Thread {
    GameStatus gameStatus;
    int time;

    public TimerThread(int time, GameStatus status) {
        this.time = time;
        this.gameStatus = status;
    }

    public int getTime() {
        return time;
    }

    @Override
    public void run() {
        while (this.gameStatus.getGameState()) {
            this.time++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
