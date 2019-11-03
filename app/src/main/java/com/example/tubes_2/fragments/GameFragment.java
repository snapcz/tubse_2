package com.example.tubes_2.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
public class GameFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, GameWrapper, JoystickView.OnMoveListener, SensorEventListener {
    FloatingActionButton shootButton, pauseButton;

    LinearLayout gameWrapper;

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

    SensorManager sensorManager;
    Sensor accelerometer, magnetometer;

    float[] magnetometerReading, accelerometerReading;

    static final float VALUE_DRIFT = 0.05f;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        this.difficulty = this.getArguments().getParcelable("difficulty");

        this.gameWrapper = view.findViewById(R.id.gameView);
        this.gameView = new GameView(this.getContext(), this);

        gameWrapper.addView(this.gameView);

        this.playerView = view.findViewById(R.id.playerView);
        this.enemyView = view.findViewById(R.id.enemyView);


        this.shootButton = view.findViewById(R.id.shoot);
        this.pauseButton = view.findViewById(R.id.pause);

        this.shootButton.setOnClickListener(this);

        if (this.difficulty.getChargeEnabled() == 1) {
            this.shootButton.setOnLongClickListener(this);
        }

        this.pauseButton.setOnClickListener(this);

        if (this.getArguments().getInt("controller") == 1) {
            this.sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            this.magnetometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            this.magnetometerReading = new float[3];
            this.accelerometerReading = new float[3];

            this.joystickView.setVisibility(View.GONE);
        } else {
            this.joystickView = view.findViewById(R.id.joystick);
            this.joystickView.setOnMoveListener(this);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (this.accelerometer != null) {
            this.sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        if (this.magnetometer != null) {
            this.sensorManager.registerListener(this, this.magnetometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try{
            this.sensorManager.unregisterListener(this);
        } catch(Exception e){

        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (this.gameStatus != null && this.gameStatus.getGameState() && this.gameStatus.getCountdown() == 0) {
            int sensorType = sensorEvent.sensor.getType();

            switch (sensorType) {
                case Sensor.TYPE_ACCELEROMETER:
                    this.accelerometerReading = sensorEvent.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    this.magnetometerReading = sensorEvent.values.clone();
                    break;
            }

            this.getMatrix();
        }
    }

    public void getMatrix() {
        final float[] orientationAngles = new float[3];
        final float[] rotationMatrix = new float[9];

        this.sensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);

        this.sensorManager.getOrientation(rotationMatrix, orientationAngles);

        float pitch = orientationAngles[1];
        float roll = orientationAngles[2];

        // azimuth = relatif terhadap kamera selfie (?)
        // pitch = relatif kalo diputer ke depan
        // roll = relatif kalo diputer nyamping (seperti barrel roll)

        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0;
        }

        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0;
        }

        this.gameStatus.movePlayer((int)Math.ceil(roll) * 2, (int)Math.ceil(pitch) * 2);
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
        this.drawer = new DrawerThread(this.getContext(), this, handler, this.gameStatus, this.gameView);
        this.attacker = new AttackThread(this.gameStatus);

        int time = 0;

        if (this.timer != null) {
            time = this.timer.getTime();
        }

        this.timer = new TimerThread(time, this.gameStatus);

        this.drawer.start();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof UIActivity) {
            this.activity = (UIActivity)context;
        }
    }

    public static GameFragment newInstance(Difficulty difficulty, int controller) {
        GameFragment fragment = new GameFragment();

        Bundle args = new Bundle();
        args.putParcelable("difficulty", difficulty);
        args.putInt("controller", controller);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == this.shootButton.getId() && this.gameStatus.getCountdown() == 0) {
            this.gameStatus.addPlayerAttack(0);
        } else if (id == this.pauseButton.getId() && this.gameStatus.getCountdown() == 0) {
            if (this.gameStatus.getGameState()) {
                this.pauseButton.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                this.drawer.drawPause();
                this.gameStatus.endGame();
                Log.d("pause", "onClick: ");
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

    @Override
    public void startLogicThread() {
        if (this.attacker != null) {
            this.attacker.start();
        }

        if (this.timer != null) {
            this.timer.start();
        }

        this.gameStatus.startAttacks();
    }

    @Override
    public void onPause() {
        super.onPause();

        this.gameWrapper.removeAllViews();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.gameView.getParent() == null) {
            this.gameWrapper.addView(this.gameView);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void calculateScore() {
        FragmentManager fm = this.getFragmentManager();

        int lifeScore = this.gameStatus.getPlayer().getCurrentHealth() * 5000;
        int initScore = lifeScore;
        int time = this.timer.getTime();

        lifeScore -= time;

        if (this.difficulty.getChargeEnabled() == 0) { // hard
            lifeScore >>= 1;
        }

        ScoringFragment fragment = ScoringFragment.newInstance(
                Integer.toString(initScore),
                "-" + time,
                (this.difficulty.getChargeEnabled() == 1 ? "1" : "2"),
                Integer.toString(lifeScore));

        fragment.show(fm, "");
    }

    @Override
    public void gameOver() {
        // u loser, show a toast to humiliate him
        this.activity.showLoser();
    }
}
