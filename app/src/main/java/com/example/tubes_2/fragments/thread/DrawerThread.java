package com.example.tubes_2.fragments.thread;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.tubes_2.R;
import com.example.tubes_2.fragments.GameView;
import com.example.tubes_2.interfaces.GameWrapper;
import com.example.tubes_2.model.Attack;
import com.example.tubes_2.model.Constant;
import com.example.tubes_2.model.Ship;
import com.example.tubes_2.presenter.GameStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DrawerThread extends Thread {
    GameStatus status;
    GameHandler handler;
    Resources res;
    SurfaceHolder gameHolder;

    GameWrapper wrapper;

    Bitmap playerBitmap, enemyBitmap, smallAttackBitmap, playerChargeBitmap, enemyChargaBitmap;

    Drawable pauseImage;

    long previousTime, fps;

    public DrawerThread(Context ctx, GameWrapper wrapper, GameHandler handler, GameStatus gameStatus, GameView gameView) {
        this.previousTime = 0;
        this.fps = 60; // PC MASTER RACE, MOBILE PEASANTS!!!

        this.status = gameStatus;
        this.gameHolder = gameView.getHolder();
        this.handler = handler;

        this.wrapper = wrapper;

        this.res = ctx.getResources();

        this.playerBitmap = BitmapFactory.decodeResource(res, R.drawable.player_ship);
        this.enemyBitmap = BitmapFactory.decodeResource(res, R.drawable.enemy_ship);
        this.smallAttackBitmap = BitmapFactory.decodeResource(res, R.drawable.small_attack);
        this.playerChargeBitmap = BitmapFactory.decodeResource(res, R.drawable.player_charge_attack);
        this.enemyChargaBitmap = BitmapFactory.decodeResource(res, R.drawable.enemy_charge_attack);

        this.pauseImage = res.getDrawable(R.drawable.ic_pause_black_24dp);
    }

    @Override
    public void run() {
        Ship player = status.getPlayer();
        Ship enemy = status.getEnemy();

        Canvas gameCanvas;

        while (status.getGameState() && status.getCountdown() > 0) {
            gameCanvas = this.gameHolder.lockCanvas();

            gameCanvas.drawColor(Color.WHITE);

            synchronized (gameHolder) {
                gameCanvas.drawBitmap(this.playerBitmap, player.getPositionX(), player.getPositionY(), null);
                gameCanvas.drawBitmap(this.enemyBitmap, enemy.getPositionX(), enemy.getPositionY(), null);

                Iterator<Attack> it = status.getAttacks().iterator();

                while (it.hasNext()) {
                    Attack atk = it.next();
                    if (!atk.isDone()) {
                        if (atk.getId() == 0) { // smallAttack
                            gameCanvas.drawBitmap(this.smallAttackBitmap, atk.getPositionX(), atk.getPositionY(), null);
                        } else { // charge
                            if (atk.getSource() == player) {
                                gameCanvas.drawBitmap(this.playerChargeBitmap, atk.getPositionX(), atk.getPositionY(), null);
                            } else {
                                gameCanvas.drawBitmap(this.enemyChargaBitmap, atk.getPositionX(), atk.getPositionY(), null);
                            }
                        }
                    } else {
                        it.remove();
                    }
                }
            }

            Paint pt = new Paint();
            pt.setColor(Color.BLACK);
            pt.setTextSize(256);

            int xPos = (gameCanvas.getWidth() / 2) - 64;
            int yPos = (int)((gameCanvas.getHeight() / 2) - ((pt.descent() + pt.ascent()) / 2));

            gameCanvas.drawText(Integer.toString(status.getCountdown()), xPos, yPos, pt);

            // just decrement here
            status.reduceCountdown();

            gameHolder.unlockCanvasAndPost(gameCanvas);

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            continue;
        }

        this.wrapper.startLogicThread();

        while (status.getGameState()) {
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMs = currentTimeMillis - previousTime;
            long sleepTimeMs = (long) (1000f/ fps - elapsedTimeMs);

            gameCanvas = this.gameHolder.lockCanvas();

            try {
                if (gameCanvas == null) {
                    Thread.sleep(1);

                    continue;
                } else {
                    gameCanvas.drawColor(Color.WHITE);

                    if (sleepTimeMs > 0) {
                        Thread.sleep(sleepTimeMs);
                    }

                    this.handler.sendUpdateHPMessage();

                    status.updateGame();

                    synchronized (gameHolder) {
                        gameCanvas.drawBitmap(this.playerBitmap, player.getPositionX(), player.getPositionY(), null);
                        gameCanvas.drawBitmap(this.enemyBitmap, enemy.getPositionX(), enemy.getPositionY(), null);

                        Iterator<Attack> it = status.getAttacks().iterator();

                        while (it.hasNext()) {
                            Attack atk = it.next();
                            if (!atk.isDone()) {
                                if (atk.getId() == 0) { // smallAttack
                                    gameCanvas.drawBitmap(this.smallAttackBitmap, atk.getPositionX(), atk.getPositionY(), null);
                                } else { // charge
                                    if (atk.getSource() == player) {
                                        gameCanvas.drawBitmap(this.playerChargeBitmap, atk.getPositionX(), atk.getPositionY(), null);
                                    } else {
                                        gameCanvas.drawBitmap(this.enemyChargaBitmap, atk.getPositionX(), atk.getPositionY(), null);
                                    }
                                }
                            } else {
                                it.remove();
                            }
                        }
                    }

                    this.gameHolder.unlockCanvasAndPost(gameCanvas);
                }
            } catch (Exception e) {
                this.gameHolder.unlockCanvasAndPost(gameCanvas);
            } finally {
                this.previousTime = currentTimeMillis;
            }
        }
    }

    public void drawPause() {
        Canvas gameCanvas = this.gameHolder.lockCanvas();

        synchronized (this) {
            this.pauseImage.draw(gameCanvas);
        }

        this.gameHolder.unlockCanvasAndPost(gameCanvas);
    }

    public void clearScreen() {
        Canvas gameCanvas = this.gameHolder.lockCanvas();

        gameCanvas.drawColor(Color.WHITE);

        this.gameHolder.unlockCanvasAndPost(gameCanvas);
    }
}
