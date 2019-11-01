package com.example.tubes_2.model.thread;

import com.example.tubes_2.presenter.GameStatus;

import java.util.Random;

public class AttackThread extends Thread {
    GameStatus status;

    public AttackThread(GameStatus gameStatus) {
        this.status = gameStatus;
    }

    @Override
    public void run() {
        while (this.status.getGameState()) {
            try {
                synchronized (this) {
                    if (this.status.getCountdown() == 0) {
                        Random rd = new Random();
                        double attackType = rd.nextDouble();

                        if (attackType <= this.status.getDifficulty().getChanceSmallAttack()) {
                            Thread.sleep(this.status.getDifficulty().getEnemyAttackTime());
                            this.status.addEnemyAttack(0);
                        }else {
                            Thread.sleep(this.status.getDifficulty().getEnemyAttackTime());
                            this.status.addEnemyAttack(1);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
