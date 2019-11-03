package com.example.tubes_2.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.tubes_2.R;
import com.example.tubes_2.model.Attack;
import com.example.tubes_2.model.Constant;
import com.example.tubes_2.model.Difficulty;
import com.example.tubes_2.model.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameStatus {
    Context ctx;
    Difficulty difficulty;

    /**
     * true bila game sudah start (walaupun dipause, dia bakal tetep true, useful for pausing!)
     * false bila game belum start atau sudah selesai
     */
    boolean gameState, kiri;

    int countdown, wd, ht;

    Ship player, enemy;

    ArrayList<Attack> attacks;

    public GameStatus(Context ctx, Difficulty difficulty, int wd, int ht) {
        this.ctx = ctx;
        this.difficulty = difficulty;
        this.gameState = false;

        this.wd = wd;
        this.ht = ht;

        this.attacks = new ArrayList<>();

        this.kiri = true;
    }

    public void initializeGame() {
        this.countdown = 3;

        this.gameState = true;
        this.kiri = true;

        Drawable playerShip = this.ctx.getResources().getDrawable(R.drawable.player_ship);
        int playerWidth = playerShip.getIntrinsicWidth();
        int playerHeight = playerShip.getIntrinsicHeight();

        Drawable enemyShip = this.ctx.getResources().getDrawable(R.drawable.enemy_ship);
        int enemyWidth = enemyShip.getIntrinsicWidth();
        int enemyHeight = enemyShip.getIntrinsicHeight();

        this.player = new Ship(this.difficulty.getPlayerHealth(), playerWidth, playerHeight,0);
        this.enemy = new Ship(this.difficulty.getEnemyHealth(), enemyWidth, enemyHeight,1);

        this.player.setPosition(wd / 2 - (playerWidth / 2), ht - playerHeight);
        this.enemy.setPosition(wd / 2 - (enemyWidth / 2), 0);

        this.attacks.clear();
    }

    public void resumeGame() {
        this.countdown = 3;
        this.gameState = true;
        ArrayList<Attack> newAttack = new ArrayList<>();
        for (int i = 0; i < attacks.size(); i++) {
            Attack curr = attacks.get(i);
            Attack atk = Attack.createAttack(curr.getSource(),curr.getIdBullet(),curr.getPositionX(),curr.getPositionY(),curr.getVelocityX(),curr.getVelocityY(), this.difficulty.getSmallAttackDamage(),this);
            newAttack.add(atk);
        }
        attacks.clear();
        for (int i = 0; i < newAttack.size(); i++) {
            attacks.add(newAttack.get(i));
        }
    }

    public void startAttacks(){
        for (int i = 0; i < attacks.size(); i++) {
            if(!attacks.get(i).isAlive())
                attacks.get(i).start();
        }
    }

    public void endGame() {
        this.gameState = false;
    }

    public void reduceCountdown() {
        this.countdown--;
    }

    public boolean getGameState() {
        return this.gameState;
    }

    public int getCountdown() {
        return countdown;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Ship getPlayer() {
        return player;
    }

    public Ship getEnemy() {
        return enemy;
    }

    public void updateGame() {
        // update enemy position

        if (this.kiri) {
            this.enemy.setPosition(this.enemy.getPositionX() - this.difficulty.getEnemyMoveLimit(), this.enemy.getPositionY());
        } else {
            this.enemy.setPosition(this.enemy.getPositionX() + this.difficulty.getEnemyMoveLimit(), this.enemy.getPositionY());
        }

        if (this.enemy.getPositionX() < 0) {
            this.enemy.setPosition(0, this.enemy.getPositionY());
            this.kiri = false;
        }

        if (this.enemy.getPositionX() + this.enemy.getWidth() > this.wd) {
            this.enemy.setPosition(this.wd - this.enemy.getWidth(), this.enemy.getPositionY());
            this.kiri = true;
        }

        if (this.enemy.getCurrentHealth() <= 0 || this.player.getCurrentHealth() <= 0) this.gameState=false;
        // check collision here, update status accordingly
    }

    public List<Attack> getAttacks() {
        return this.attacks;
    }

    public float getPlayerHealthPercentage() {
        return (float)this.player.getCurrentHealth() / (float)this.player.getMaxHealth();
    }

    public float getEnemyHealthPercentage() {
        return (float)this.enemy.getCurrentHealth() / (float)this.enemy.getMaxHealth();
    }

    public void movePlayer(int posX, int posY) {
        /**
         * TODO: Check Bound!
         *
         * I'll tell you later why the calculation looks like this
         */
        if(!((this.player.getPositionX()+posX)<0) &&
                !((this.player.getPositionX()+posX)>this.wd) &&
                !((this.player.getPositionY()+posY)<0)){
            this.player.setPosition(this.player.getPositionX() + posX, this.player.getPositionY() - posY);
        }
    }

    public void addPlayerAttack(int id) {
        //int smallAttackPosition = (player.getPositionX() + player.getWidth() / 2) - (this.smallAttackWidth / 2);

        //this.status.addAttack(Attack.createAttack(enemy, 1, smallAttackPosition, enemy.getHeight()));

        int smallAttackPositionX = (player.getPositionX() + player.getWidth() / 2) - (Constant.SMALL_ATTACK_WIDTH / 2);
        int chargeAttackPositionX = (player.getPositionX() + player.getWidth() / 2) - (Constant.PLAYER_CHARGE_ATTACK_WIDTH / 2);
        Attack atk;
        if (id == 0) {
            atk = Attack.createAttack(this.player, id,
                    smallAttackPositionX, player.getPositionY(),
                    Constant.SMALL_ATTACK_SPEED_X, -Constant.SMALL_ATTACK_SPEED_Y,
                    this.difficulty.getSmallAttackDamage(),this);
            this.attacks.add(atk);
        } else {
            atk = Attack.createAttack(this.player, id,
                    chargeAttackPositionX, player.getPositionY(), Constant.CHARGE_ATTACK_SPEED_X, -Constant.CHARGE_ATTACK_SPEED_Y, this.difficulty.getPlayerChargeAttackDamage(),this);
            this.attacks.add(atk);
        }

        atk.start();
    }

    public void addEnemyAttack(int id) {
        int smallAttackPositionX = (enemy.getPositionX() + enemy.getWidth() / 2) - (Constant.SMALL_ATTACK_WIDTH / 2);
        int chargeAttackPositionX = (enemy.getPositionX() + enemy.getWidth() / 2) - (Constant.ENEMY_CHARGE_ATTACK_WIDTH / 2);

        int smallAttackPositionY = enemy.getPositionY() + enemy.getHeight() + Constant.SMALL_ATTACK_HEIGHT;
        int chargeAttackPositionY = enemy.getPositionY() + enemy.getHeight() + Constant.ENEMY_CHARGE_ATTACK_HEIGHT;

        Attack atk;
        if (id == 0) {
            atk = Attack.createAttack(enemy, id, smallAttackPositionX, smallAttackPositionY, Constant.SMALL_ATTACK_SPEED_X, Constant.SMALL_ATTACK_SPEED_Y, this.difficulty.getSmallAttackDamage(),this);
            this.attacks.add(atk);
        } else {
            atk =Attack.createAttack(enemy, id, chargeAttackPositionX, chargeAttackPositionY, Constant.CHARGE_ATTACK_SPEED_X, Constant.CHARGE_ATTACK_SPEED_Y, this.difficulty.getEnemyChargeAttackDamage(),this);
            this.attacks.add(atk);
        }

        atk.start();
    }
}
