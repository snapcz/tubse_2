package com.example.tubes_2.model;

import android.graphics.Point;
import android.util.Log;

import com.example.tubes_2.presenter.GameStatus;

import java.util.Random;

public class Attack extends Thread {
    protected Ship source;
    protected GameStatus status;
    protected int positionX, positionY, velocityX, velocityY;
    protected int id, damage;
    protected boolean done;
    protected long previousTime, fps;

    public Attack(Ship source, int positionX, int positionY, int velocityX, int velocityY, int damage,int id,GameStatus status) {
        this.source = source;
        this.positionX = positionX;
        this.positionY = positionY;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.id = id;
        this.damage = damage;
        this.status = status;
        this.done = false;
        this.previousTime = 0;
        this.fps = 60; // PC MASTER RACE, MOBILE PEASANTS!!!

    }

    public static Attack createAttack(Ship source, int id, int positionX, int positionY, int velocityX, int velocityY, int damage,GameStatus status) {
        return new Attack(source, positionX, positionY, velocityX, velocityY, damage,id,status);
    }

    public Ship getSource() {
        return this.source;
    }

    public Point getPosition() {
        return new Point(this.positionX, this.positionY);
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getIdBullet() {
        return this.id;
    }

    public int getDamage() {
        return this.damage;
    }

    public void move() {
        this.positionX += this.velocityX;
        this.positionY += this.velocityY;
    }

    public int getVelocityX(){return this.velocityX;}

    public int getVelocityY(){return this.velocityY;}


    public boolean isDone(){return this.done;}

    @Override
    public void run(){
        while (status.getGameState()) {
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMs = currentTimeMillis - previousTime;
            long sleepTimeMs = (long) (1000f/ fps - elapsedTimeMs);
            try {
                synchronized (this){
                    if(this.status.getCountdown()==0){
                        if(sleepTimeMs>0){
                            Thread.sleep(sleepTimeMs);
                        }
                        this.move();
                        //YO DEBUG YO
                        if(!this.done && this.source.getId()==0){
                            Ship collision = this.status.getEnemy();

                            if(this.positionY>this.status.getHeight()){
                                done=true;
                                break;
                            }

                            if(this.getIdBullet()==0){
                                if(this.positionY < (collision.getPositionY()+collision.getHeight()) &&
                                        this.positionX>=collision.getPositionX() &&
                                        (this.positionY+Constant.SMALL_ATTACK_HEIGHT) > collision.getPositionY() &&
                                        (this.positionX+Constant.SMALL_ATTACK_WIDTH)<=(collision.getPositionX()+collision.getWidth())){
                                    collision.damageShip(this.damage);
                                    done=true;
                                    break;
                                }
                            } else {
                                if(this.positionY<(collision.getPositionY()+collision.getHeight()) &&
                                        this.positionX>=collision.getPositionX() &&
                                        (this.positionY+Constant.PLAYER_CHARGE_ATTACK_HEIGHT) > collision.getPositionY() &&
                                        (this.positionX+Constant.PLAYER_CHARGE_ATTACK_WIDTH)<=(collision.getPositionX()+collision.getWidth())){
                                    collision.damageShip(this.damage);
                                    done=true;
                                    break;
                                }
                            }
                        }
                        else if(!this.done && this.source.getId()==1){
                            Ship collision = this.status.getPlayer();
                            if(this.positionY>this.status.getHeight()){
                                done=true;
                                break;
                            }
                            if(this.getIdBullet()==0){
                                if((this.positionY+ Constant.SMALL_ATTACK_HEIGHT)>collision.getPositionY() &&
                                        this.positionX>=collision.getPositionX() &&
                                        this.positionX<=(collision.getPositionX()+collision.getWidth()) &&
                                        this.positionY <= (collision.getPositionY()+collision.getHeight())){
                                    collision.damageShip(this.damage);
                                    done=true;
                                    break;
                                }
                            } else {
                                if ((this.positionY+ Constant.ENEMY_CHARGE_ATTACK_HEIGHT)>collision.getPositionY() &&
                                        this.positionY <= (collision.getPositionY()+collision.getHeight())){
                                    if(this.positionX<collision.getPositionX()){
                                        if(this.positionX+Constant.ENEMY_CHARGE_ATTACK_WIDTH>collision.getPositionX()){
                                            collision.damageShip(this.damage);
                                            done=true;
                                            break;
                                        }
                                    } else {
                                        if(this.positionX<=collision.getPositionX()+collision.getWidth()){
                                            collision.damageShip(this.damage);
                                            done=true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.previousTime = currentTimeMillis;
            }
        }
    }
}
