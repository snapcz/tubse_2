package com.example.tubes_2.model;

import android.graphics.Point;

public class Ship {
    protected int health, maxHealth;

    protected int width, height;

    protected int positionX, positionY;

    protected int id;

    public Ship(int health, int width, int height, int id) {
        this.health = health;
        this.maxHealth = health;
        this.width = width;
        this.height = height;
        this.id = id;
    }

    public int getId(){return this.id;}

    public int getCurrentHealth() {
        return this.health;
    }

    public int getMaxHealth() { return this.maxHealth; }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
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

    public void setPosition(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    public void damageShip(int damage) {
        this.health -= damage;
    }

    public boolean isDead() {
        return this.health <= 0;
    }
}
