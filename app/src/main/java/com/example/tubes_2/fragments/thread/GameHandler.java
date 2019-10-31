package com.example.tubes_2.fragments.thread;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.tubes_2.interfaces.GameWrapper;


public class GameHandler extends Handler {
    private final static int DRAW_HP_BAR = 0;
    private GameWrapper gameWrapper;

    public GameHandler(GameWrapper gameWrapper) {
        this.gameWrapper = gameWrapper;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.what == DRAW_HP_BAR) {
            this.gameWrapper.drawHPBar();
        }
    }

    public void sendUpdateHPMessage() {
        this.sendEmptyMessage(DRAW_HP_BAR);
    }
}
