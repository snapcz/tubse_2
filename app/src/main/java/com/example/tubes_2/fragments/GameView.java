package com.example.tubes_2.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.tubes_2.interfaces.GameWrapper;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    GameWrapper wrapper;

    public GameView(Context ctx, GameWrapper wrapper) {
        super(ctx);
        this.wrapper = wrapper;

        this.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.wrapper.initializeGame();
        this.wrapper.startGame();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // do nothing atm
    }
}
