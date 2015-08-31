package com.artem.seabattle;

/**
 * Created by artem kupchinskiy on 03.08.2015.
 */


public class Ship {
    public static class Debris extends Ship {
        public Debris() {
            super(0);
        }
    }
    public static final Debris deadShip = new Debris();
    private final int size;
    private int lives;
    private boolean alive;
    public Ship(int size) {
        this.size = size;
        this.lives = size;
        this.alive = true;
    }

    public void beDamaged() {
        lives--;
        if (lives <= 0) {
            alive = false;
            System.out.printf("The ship of size %d is sunk!\n", size);
        }
    }
    public boolean isAlive() {
        return alive;
    }
}
