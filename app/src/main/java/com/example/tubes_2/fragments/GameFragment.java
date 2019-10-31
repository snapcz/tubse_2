package com.example.tubes_2.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.tubes_2.R;
import com.example.tubes_2.fragments.thread.DrawerThread;
import com.example.tubes_2.fragments.thread.GameHandler;
import com.example.tubes_2.interfaces.GameWrapper;
import com.example.tubes_2.interfaces.UIActivity;
import com.example.tubes_2.model.thread.TimerThread;
import com.example.tubes_2.presenter.GameStatus;
import com.example.tubes_2.model.Difficulty;
import com.example.tubes_2.model.thread.AttackThread;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.github.controlwear.virtual.joystick.android.JoystickView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, GameWrapper, JoystickView.OnMoveListener {
    FloatingActionButton shootButton, pauseButton;

    DrawerThread drawer;
    AttackThread attacker;
    TimerThread timer;

    GameView gameView;
    ImageView playerView, enemyView;

    Canvas playerBar, enemyBar;

    JoystickView joystickView;

    GameStatus gameStatus;

    Difficulty difficulty;

    UIActivity activity;

    public GameFragment() {
        // Required empty public constructor
    }

    public void setActivity(UIActivity activity){
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        this.difficulty = this.getArguments().getParcelable("difficulty");

        LinearLayout gameWrapper = view.findViewById(R.id.gameView);
        this.gameView = new GameView(this.getContext(), this);
        gameWrapper.addView(this.gameView);

        this.playerView = view.findViewById(R.id.playerView);
        this.enemyView = view.findViewById(R.id.enemyView);

        this.joystickView = view.findViewById(R.id.joystick);

        this.shootButton = view.findViewById(R.id.shoot);
        this.pauseButton = view.findViewById(R.id.pause);

        this.shootButton.setOnClickListener(this);
        if (this.difficulty.getChargeEnabled() == 1) {
            this.shootButton.setOnLongClickListener(this);
        }
        this.pauseButton.setOnClickListener(this);

        this.joystickView.setOnMoveListener(this);

        return view;
    }

    public void initializeGame() {
        if (this.gameStatus == null) {
            this.gameStatus = new GameStatus(this.getContext(), this.difficulty, this.gameView.getWidth(), this.gameView.getHeight());
        }

        this.gameStatus.initializeGame();
    }

    public void startGame() {
        if (this.playerBar == null || this.enemyBar == null) {
            Bitmap pbmp = Bitmap.createBitmap(this.playerView.getWidth(), this.playerView.getHeight(), Bitmap.Config.ARGB_8888);
            this.playerView.setImageBitmap(pbmp);

            this.playerBar = new Canvas(pbmp);

            Bitmap ebmp = Bitmap.createBitmap(this.enemyView.getWidth(), this.enemyView.getHeight(), Bitmap.Config.ARGB_8888);
            this.enemyView.setImageBitmap(ebmp);

            this.enemyBar = new Canvas(ebmp);
        }

        GameHandler handler = new GameHandler(this);
        this.drawer = new DrawerThread(this.getContext(), handler, this.gameStatus, this.gameView);
        this.attacker = new AttackThread(this.gameStatus);

        int time = 0;

        if (this.timer != null) {
            time = this.timer.getTime();
        }

        this.timer = new TimerThread(time, this.gameStatus);

        this.attacker.start();
        this.drawer.start();
        this.timer.start();
    }

    public static GameFragment newInstance(Difficulty difficulty,UIActivity activity) {
        GameFragment fragment = new GameFragment();

        Bundle args = new Bundle();
        args.putParcelable("difficulty", difficulty);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();


        if (id == this.shootButton.getId()) {
            //this.gameStatus.addPlayerAttack();
        }
        if (id == this.shootButton.getId() && this.gameStatus.getCountdown() == 0) {
            this.gameStatus.addPlayerAttack(0);
        } else if (id == this.pauseButton.getId() && this.gameStatus.getCountdown() == 0) {
            if (this.gameStatus.getGameState()) {
                this.pauseButton.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                this.drawer.drawPause();
                this.gameStatus.endGame();
            } else {
                this.pauseButton.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                this.gameStatus.resumeGame();
                this.startGame();
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        if (id == this.shootButton.getId() && this.gameStatus.getCountdown() == 0 && this.gameStatus.getDifficulty().getChargeEnabled() == 1) {
            this.gameStatus.addPlayerAttack(1);
        }

        return true;
    }

    public void drawHPBar() {
        this.playerBar.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        this.enemyBar.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Paint red = new Paint();
        red.setStyle(Paint.Style.FILL);
        red.setColor(Color.RED);

        Paint blue = new Paint();
        blue.setStyle(Paint.Style.FILL);
        blue.setColor(Color.BLUE);

        float playerBarLength = this.gameStatus.getPlayerHealthPercentage() * this.playerBar.getWidth();
        float enemyBarLength = this.gameStatus.getEnemyHealthPercentage() * this.enemyBar.getWidth();

        this.playerBar.drawRect(0, 0,   playerBarLength, this.playerBar.getHeight(), blue);
        this.enemyBar.drawRect(0, 0, enemyBarLength, this.enemyBar.getHeight(), red);

        this.playerView.invalidate();
        this.enemyView.invalidate();
    }

    @Override
    public void onMove(int angle, int strength) {
        if (this.gameStatus.getCountdown() == 0) {
            int multiplier = (int)Math.ceil((double)strength / 10);

            int moveX = (int)Math.ceil(Math.cos(Math.toRadians(angle)) * multiplier);
            int moveY = (int)Math.ceil(Math.sin(Math.toRadians(angle)) * multiplier);

            this.gameStatus.movePlayer(moveX, moveY);
        }
    }
}
