package com.example.tubes_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tubes_2.fragments.GameFragment;
import com.example.tubes_2.fragments.HighScoreFragment;
import com.example.tubes_2.fragments.MenuFragment;
import com.example.tubes_2.fragments.SettingsFragment;
import com.example.tubes_2.interfaces.UIActivity;
import com.example.tubes_2.model.Difficulty;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UIActivity {
    FragmentManager fragmentManager;
    Fragment[] fragmentList;
    final int MENU = 0;
    final int INIT_GAME = 1;
    final int START_GAME = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fragmentManager = this.getSupportFragmentManager();
        this.fragmentList = new Fragment[3];
        this.fragmentList[0] = MenuFragment.newInstance(this);
        this.fragmentList[1] = (SettingsFragment.newInstance());
        this.changePage(MENU);
    }

    @Override
    public void changePage(int code) {
        FragmentTransaction ft = this.fragmentManager.beginTransaction();
        int chosenPage = -1;
        switch(code){
            case START_GAME:
                chosenPage = START_GAME;
                break;
            case MENU:
                chosenPage = MENU;
                break;
            case INIT_GAME:
                chosenPage = INIT_GAME;
                break;
             default:
                break;
        }
        if(this.fragmentList[chosenPage].isAdded()) ft.show(this.fragmentList[chosenPage]);
        else{
            ft.add(R.id.fragment_container,this.fragmentList[chosenPage]).addToBackStack("");
            ft.show(this.fragmentList[chosenPage]);
        }
        for (int i = 0; i < this.fragmentList.length; i++) {
            if(this.fragmentList[i]!=this.fragmentList[chosenPage]){
                if(this.fragmentList[i]!=null&&this.fragmentList[i].isAdded()) ft.remove(this.fragmentList[i]);
            }
        }
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        if(this.fragmentList[START_GAME]!=null && this.fragmentList[START_GAME].isVisible()){
            this.changePage(0);
        }
        else{
            if(this.fragmentList[MENU].isVisible()){
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Are you sure to quit?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            else{
                super.onBackPressed();
            }
        }
    }

    @Override
    public void updateScore(int score) {
        this.changePage(MENU);

        MenuFragment fragment = (MenuFragment)this.fragmentList[0];

        fragment.updateScore(score);
    }

    @Override
    public void showLoser() {
        this.changePage(MENU);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("You Loser!");

                builder.setMessage("You lose lol, sucks to be you!");

                builder.setPositiveButton(R.string.cries, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.setCancelable(false);

                dialog.show();
            }
        });
    }

    @Override
    public void startGame(int difficulty, int controller) {
        this.fragmentList[START_GAME] = (GameFragment.newInstance(Difficulty.createDifficulty(difficulty),controller));
        this.changePage(START_GAME);
    }
}
