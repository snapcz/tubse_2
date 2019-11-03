package com.example.tubes_2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Difficulty implements Parcelable {
    // Apakah player bisa charge attack?
    private int chargeEnabled;

    // darah kapal
    private int playerHealth;
    private int enemyHealth;

    // peluang musuh bisa nembak attack X
    private double chanceSmallAttack;

    // waktu attack musuh
    private int enemyAttackTime;

    // attack damage
    private int smallAttackDamage;
    private int playerChargeAttackDamage;
    private int enemyChargeAttackDamage;

    // batas gerak musuh
    private int enemyMoveLimit;

    protected Difficulty(Parcel in) {
        this.chargeEnabled = in.readInt();
        this.playerHealth = in.readInt();
        this.enemyHealth = in.readInt();
        this.chanceSmallAttack = in.readDouble();
        this.enemyAttackTime = in.readInt();
        this.smallAttackDamage = in.readInt();
        this.playerChargeAttackDamage = in.readInt();
        this.enemyChargeAttackDamage = in.readInt();
        this.enemyMoveLimit = in.readInt();
    }

    private Difficulty(
            int chargeEnabled,
            int playerHealth,
            int enemyHealth,
            double chanceSmallAttack,
            int enemyAttackTime,
            int smallAttackDamage,
            int playerChargeAttackDamage,
            int enemyChargeAttackDamage,
            int enemyMoveLimit) {
        this.chargeEnabled = chargeEnabled;
        this.playerHealth = playerHealth;
        this.enemyHealth = enemyHealth;
        this.chanceSmallAttack = chanceSmallAttack;
        this.enemyAttackTime = enemyAttackTime;
        this.smallAttackDamage = smallAttackDamage;
        this.enemyChargeAttackDamage = enemyChargeAttackDamage;
        this.playerChargeAttackDamage = playerChargeAttackDamage;
        this.enemyMoveLimit = enemyMoveLimit;
    }

    public double getChanceSmallAttack() {
        return chanceSmallAttack;
    }

    public int getEnemyAttackTime() {
        return enemyAttackTime;
    }

    public int getEnemyHealth() {
        return enemyHealth;
    }

    public int getEnemyMoveLimit() {
        return enemyMoveLimit;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }

    public int getSmallAttackDamage() {
        return smallAttackDamage;
    }

    public int getEnemyChargeAttackDamage() {
        return enemyChargeAttackDamage;
    }

    public int getPlayerChargeAttackDamage() {
        return playerChargeAttackDamage;
    }

    public int getChargeEnabled() {
        return chargeEnabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.chargeEnabled);
        parcel.writeInt(this.playerHealth);
        parcel.writeInt(this.enemyHealth);
        parcel.writeDouble(this.chanceSmallAttack);
        parcel.writeInt(this.enemyAttackTime);
        parcel.writeInt(this.smallAttackDamage);
        parcel.writeInt(this.enemyChargeAttackDamage);
        parcel.writeInt(this.playerChargeAttackDamage);
        parcel.writeInt(this.enemyMoveLimit);
    }

    public static final Parcelable.Creator<Difficulty> CREATOR = new Parcelable.Creator<Difficulty>() {
        @Override
        public Difficulty createFromParcel(Parcel source) {
            return new Difficulty(source);
        }

        @Override
        public Difficulty[] newArray(int size) {
            return new Difficulty[size];
        }
    };

    public static Difficulty createDifficulty(int id) {
        int charge = 0;
        int playerLife = 50;
        int enemyLife = 500;
        double chanceSmallAttack = 0.4;
        int enemyAttackTime = 850;
        int enemyChargeAttackDamage = 50;
        int playerChargeAttackDamage = 15;

        if (id == 0) { // normal difficulty
            charge = 1;
            playerLife = 100;
            enemyLife = 350;
            chanceSmallAttack = 0.6;
            enemyAttackTime = 1000;
            enemyChargeAttackDamage = 30;
            playerChargeAttackDamage = 20;
        }

        return new Difficulty(charge, playerLife, enemyLife, chanceSmallAttack, enemyAttackTime, 10, playerChargeAttackDamage, enemyChargeAttackDamage, 5);
    }
}
