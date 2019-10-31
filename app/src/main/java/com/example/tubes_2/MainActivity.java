package com.example.tubes_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.example.tubes_2.fragments.GameFragment;
import com.example.tubes_2.fragments.HighScoreFragment;
import com.example.tubes_2.fragments.MenuFragment;
import com.example.tubes_2.interfaces.UIActivity;
import com.example.tubes_2.model.Difficulty;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UIActivity {
    FragmentManager fragmentManager;
    List<Fragment> fragmentList;
    final int MENU = 0;
    final int START_GAME = 1;
    final int HIGH_SCORE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fragmentManager = this.getSupportFragmentManager();
        this.fragmentList = new ArrayList<>();
        this.fragmentList.add(MenuFragment.newInstance(this));
        this.fragmentList.add(GameFragment.newInstance(Difficulty.createDifficulty(0),this));
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
            case HIGH_SCORE:
                chosenPage = HIGH_SCORE;
                break;
            case MENU:
                chosenPage = MENU;
                break;
             default:
                break;
        }
        if(this.fragmentList.get(chosenPage).isAdded()) ft.show(this.fragmentList.get(chosenPage));
        else{
            ft.add(R.id.fragment_container,this.fragmentList.get(chosenPage)).addToBackStack("");
            ft.show(this.fragmentList.get(chosenPage));
        }
        for (int i = 0; i < this.fragmentList.size(); i++) {
            if(i!=chosenPage){
                if(this.fragmentList.get(i).isAdded()) ft.hide(this.fragmentList.get(i));
            }
        }
        ft.commit();
    }


    @Override
    public void onBackPressed(){
        if(this.fragmentManager.getBackStackEntryCount()<2){
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

    @Override
    public void updateScore(int score) {
        this.changePage(MENU);

        MenuFragment fragment = (MenuFragment)this.fragmentList.get(0);

        fragment.updateScore(score);
    }
}
