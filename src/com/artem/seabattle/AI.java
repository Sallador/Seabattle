package com.artem.seabattle;

import java.util.Random;
import static com.artem.seabattle.Field.SIZE;
/**
 * Created by salla_000 on 28.08.2015.
 */
class Shoot implements Comparable<Shoot> {
    private static final Random rnd = new Random();
    public static final int MAX_PRIORITY = 20;
    public static final int MIN_PRIORITY = -20;
    public static final int AVG_PRIORITY = 10;
    public final int X;
    public final int Y;
    private int priority;

    Shoot(int x, int y) {
        this.X = x;
        this.Y = y;
        this.priority = rnd.nextInt(AVG_PRIORITY);
    }
    
    Shoot(Shoot other) {
        this.X = other.X;
        this.Y = other.Y;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Shoot o) {
        return o.priority - this.priority;
    }
}

public class AI extends Player {
    private IndexMinPQ<Shoot> shoots = new IndexMinPQ<Shoot>(SIZE * SIZE);
    public AI() {
        int counter = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                shoots.insert(counter++, new Shoot(i, j));
            }
        }
        field.placeShipsRandomly();
    }
    public ShootState shootEnemy(Player villain) {
        Shoot shoot = shoots.minKey();
        int idx = shoots.minIndex();
        ShootState result = shootEnemy(villain, shoot.X, shoot.Y);
        priorityUpdate(shoot, result);
        return result;
    }
    
    private void priorityUpdate(Shoot shoot, ShootState result) {
        int x = shoot.X;
        int y = shoot.Y;
        changePriority(new Shoot(x, y), Shoot.MIN_PRIORITY);
        switch (result) {
            case HURT:
                changePriority(new Shoot(x + 1, y), Shoot.MAX_PRIORITY);
                changePriority(new Shoot(x - 1, y), Shoot.MAX_PRIORITY);
                changePriority(new Shoot(x, y - 1), Shoot.MAX_PRIORITY);
                changePriority(new Shoot(x, y + 1), Shoot.MAX_PRIORITY);
                changePriority(new Shoot(x + 1, y + 1), Shoot.MIN_PRIORITY);
                changePriority(new Shoot(x + 1, y - 1), Shoot.MIN_PRIORITY);
                changePriority(new Shoot(x - 1, y + 1), Shoot.MIN_PRIORITY);
                changePriority(new Shoot(x - 1, y - 1), Shoot.MIN_PRIORITY);
                break;
            case KILLED:
                changePriority(new Shoot(x + 1, y), Shoot.MIN_PRIORITY);
                changePriority(new Shoot(x - 1, y), Shoot.MIN_PRIORITY);
                changePriority(new Shoot(x, y - 1), Shoot.MIN_PRIORITY);
                changePriority(new Shoot(x, y + 1), Shoot.MIN_PRIORITY);
                changePriority(new Shoot(x + 1, y + 1), Shoot.MIN_PRIORITY);
                changePriority(new Shoot(x + 1, y - 1), Shoot.MIN_PRIORITY);
                changePriority(new Shoot(x - 1, y + 1), Shoot.MIN_PRIORITY);
                changePriority(new Shoot(x - 1, y - 1), Shoot.MIN_PRIORITY);
        }
    }
    
    private void changePriority(Shoot shoot, int priority) {
        if (shoot.X < 0 || shoot.Y < 0 || shoot.X >= SIZE || shoot.Y >= SIZE) {
            return;
        }
        int idx = index(shoot.X, shoot.Y);
        Shoot fromQueue = shoots.keyOf(idx);
        // избегаем случайного повторного выстрела по заведомо пустым ячейкам
        if (fromQueue.getPriority() > Shoot.MIN_PRIORITY) {
            fromQueue.setPriority(priority);
            shoots.changeKey(idx, fromQueue);
        }
    }
    
    private int index(int i, int j) {
        return i * SIZE + j;
    }

}
