package com.artem.seabattle;

import java.util.Random;
import static com.artem.seabattle.Ship.deadShip;

/**
 * Created by artem kupchinskiy on 03.08.2015.
 */
public class Field {
    public static final int SIZE = 10;
    public int shipsCount = 0;
    private char[][] userView = new char[SIZE][SIZE];
    public static enum Directions {
        LEFT, RIGHT, UP, DOWN;
    }
    // игровое поле, видимое пользователю


    /*
     внутренняя структура с расположением всех кораблей. Клетки с кораблями хрянят ссылку на соответствующий объект класса
      Ship, пустые клетки - значение null соответственно.
    */
    private final Ship[][] shootsMap = new Ship[SIZE][SIZE];

    public Field() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                userView[i][j] = '.';
            }
        }
    }

    public void update(int i, int j, char ch) {
        this.userView[i][j] = ch;
    }

    public char getCeil(int i, int j) {
        return userView[i][j];
    }

    public Ship getShip(int x, int y) {
        if (x < 0 || y >= SIZE) {
            throw new IllegalArgumentException();
        }
        return shootsMap[x][y];
    }

    public void markDamage(int x, int y) {
        if (x < 0 || y >= SIZE) {
            throw new IllegalArgumentException();
        }
        shootsMap[x][y] = deadShip;
    }
    public void placeShipsRandomly() {
        // размещаем корабли в количестве, соответствующем стандартным правилам морского боя
        for (int i = 4; i > 0; i--) {
            for (int j = 0; j < 5 - i ; j++) {
                placeOneShipRandomly(i);
            }
        }
    }
    public boolean tryPlaceOneShip(int x, int y, int number, Directions dir) {
        Ship ship;
        switch (dir) {
            case UP:
                for (int i = 0; i < number; i++) {
                    if (!isPlaceable(x, y - i)) {
                        return false;
                    }
                }
                ship = new Ship(number);
                for (int i = 0; i < number; i++) {
                    shootsMap[x][y - i] = ship;
                    userView[x][y - i] = 'K';
                }
                return true;
            case DOWN:
                for (int i = 0; i < number; i++) {
                    if (!isPlaceable(x, y + i)) {
                        return false;
                    }
                }
                ship = new Ship(number);
                for (int i = 0; i < number; i++) {
                    shootsMap[x][y + i] = ship;
                    userView[x][y + i] = 'K';
                }
                return true;
            case LEFT:
                for (int i = 0; i < number; i++) {
                    if (!isPlaceable(x - i, y)) {
                        return false;
                    }
                }
                ship = new Ship(number);
                for (int i = 0; i < number; i++) {
                    shootsMap[x - i][y] = ship;
                    userView[x - i][y] = 'K';
                }
                return true;
            case RIGHT:
                for (int i = 0; i < number; i++) {
                    if (!isPlaceable(x + i, y)) {
                        return false;
                    }
                }
                ship = new Ship(number);
                for (int i = 0; i < number; i++) {
                    shootsMap[x + i][y] = ship;
                    userView[x + i][y] = 'K';
                }
                return true;
            default:
                throw new IllegalArgumentException();
        }

    }

    public int getShipsCount() {
        return shipsCount;
    }

    private void placeOneShipRandomly(int number) {
        /*
         выбираем случайную точку старта и направление для размещения корабля размера number.
         В случае неудачи генерируем новые координаты и продолжаем до тех пор, пока не удастся
         разместить корабль корректно (без соприкосновений, в том числе и по углам)
          */
        boolean isPlaced = false;
        while (!isPlaced) {
            // генерируем случайные координаты
            Random rnd = new Random();
            int x = rnd.nextInt(SIZE);
            int y = rnd.nextInt(SIZE);
            Directions dir = Directions.values()[rnd.nextInt(4)];
            isPlaced = tryPlaceOneShip(x, y, number, dir);
        }
        shipsCount++;
    }

    public void setShipsCount(int shipsCount) {
        this.shipsCount = shipsCount;
    }

    private boolean isPlaceable(int x, int y) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE || shootsMap[x][y] != null)
            return false;
        if (check(x-1, y-1) && check(x-1, y+1) && check(x+1, y-1) && check(x+1, y+1)) {
            return check(x-1, y) && check(x+1, y) && check(x, y+1) && check(x, y-1);
        } else {
            return false;
        }
    }
    // вспомогательная функция для разрешения проблем с размерностью массива
    private boolean check(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
            return true;
        } else {
            return shootsMap[x][y] == null;
        }
    }
}
