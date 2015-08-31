package com.artem.seabattle;

import static  com.artem.seabattle.Field.*;
/**
 * Created by salla_000 on 28.08.2015.
 */
public class Player {
    protected final Field field = new Field();
    private final String name;
    private final char[][] ownShoots = new char[SIZE][SIZE];
    //private final char[][] ownShips = new char[SIZE][SIZE];


    public Player() {
        name = "Bot";
    }

    public Player(String name) {
        this.name = name;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                ownShoots[i][j] = '.';
            }
        }
    }

    public boolean isAlive() {
        return field.getShipsCount() > 0;
    }
    public String getName() {
        return name;
    }

    public int getShipsCount() {
        return field.getShipsCount();
    }

    public void placeShipsRandomly() {
        field.placeShipsRandomly();
    }

    public ShootState shootEnemy(Player villain, int x, int y) {
        if (x < 0 || y >= SIZE) {
            throw new IllegalArgumentException();
        }

        ShootState result =  villain.beShot(x, y);
        switch (result) {
            case REPEAT:
                break;
            case MISS:
                ownShoots[x][y] = 'O';
                break;
            case HURT:
                ownShoots[x][y] = 'X';
                break;
            case KILLED:
                ownShoots[x][y] = 'X';
                break;
        }
        return result;

    }

    public boolean tryPlaceOneShip(int x, int y, int number, Directions dir) {
        boolean result = field.tryPlaceOneShip(x, y, number, dir);
        if (result) {
            switch (dir) {
                case UP:
                    for (int i = 0; i < number; i++) {
                        field.update(x, y - i, 'K');
                    }
                    break;
                case DOWN:
                    for (int i = 0; i < number; i++) {
                        field.update(x, y + i, 'K');
                    }
                    break;
                case LEFT:
                    for (int i = 0; i < number; i++) {
                        field.update(x - i, y, 'K');
                    }
                    break;
                case RIGHT:
                    for (int i = 0; i < number; i++) {
                        field.update(x + i, y, 'K');
                    }
                    break;
            }
        }
        return result;
    }

    private String getOwnShips() {
        StringBuilder view = new StringBuilder();
        view.append("Расположение ваших кораблей:\n");
        view.append("  A B C D E F G H I J\n");
        for (int i = 0; i < SIZE; i++) {
            view.append("" + (i + 1) +" ");
            for (int j = 0; j < SIZE; j++) {
                view.append(field.getCeil(j, i));
                String gap = " ";
                view.append(gap);
            }
            view.append("\n");
        }
        return view.toString();
    }

    public void showField() {
        String shipsView = getOwnShips();
        String shootsView = getOwnShoots();
        String gap = "           ";
        String[] shipsLines = shipsView.split("\n");
        String[] shootsLines = shootsView.split("\n");
        assert shipsLines.length == shootsLines.length;
        StringBuilder newView = new StringBuilder();
        for (int i = 0; i < shipsLines.length; i++) {
            newView.append(shipsLines[i]).append(gap).
                    append(shootsLines[i]).append('\n');
        }
        System.out.println(newView);
    }

    private String getOwnShoots() {
        StringBuilder view = new StringBuilder("Поле, где находятся корабли противника:\n");
        view.append("  A B C D E F G H I J\n");
        for (int i = 0; i < SIZE; i++) {
            view.append("" + (i + 1) +" ");
            for (int j = 0; j < SIZE; j++) {
                view.append(ownShoots[j][i]);
                String gap = " ";
                view.append(gap);
            }
            view.append("\n");
        }
        return view.toString();
    }

    private ShootState beShot(int x, int y) {
        assert x >= 0 && y < SIZE;
        Ship ship = field.getShip(x, y);
        if (ship == null) {
            field.update(x, y, 'O');
            return ShootState.MISS;
        } else if (ship instanceof Ship.Debris) {
            return ShootState.REPEAT;
        } else {
            ship.beDamaged();
            field.markDamage(x, y);
            field.update(x, y, 'D');
            if (ship.isAlive()) {
                return ShootState.HURT;
            } else {
                field.setShipsCount(field.getShipsCount() - 1);
                return ShootState.KILLED;
            }
        }
    }
}
