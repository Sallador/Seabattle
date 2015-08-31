package com.artem.seabattle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static  com.artem.seabattle.Field.*;
/**
 * Created by salla_000 on 28.08.2015.
 */
public class Game {
    private static Player player;
    private static final AI bot = new AI();
    private static BufferedReader reader;
    public static void placeShips() throws IOException {
        System.out.println("Наберите yes, если хотите размещать корабли сами, иначе мы сгенерируем" +
                " случайное расположение");
        String request = reader.readLine();
        if (request.equals("yes")) {
            for (int i = 4; i > 0; i--) {
                for (int j = 0; j < 5 - i ; j++) {
                    requestPlaceShip(i);
                }
            }
        } else {
            player.placeShipsRandomly();
            player.showField();
        }
    }
    public static void game2() throws IOException, InterruptedException {
        while(player.isAlive() && bot.isAlive()) {
            ShootState playerResult, botResult;
            do {
                player.showField();
                playerResult = requestPlayerToShoot();
                System.out.printf("Результат выстрела игрока %s: ", player.getName());
                System.out.println(getMessageAfterShoot(playerResult));
            } while (playerResult != ShootState.MISS);
            System.out.println("Ждем хода компьютера...");
            Thread.currentThread().sleep(3000);
            do {
                botResult = bot.shootEnemy(player);
                System.out.printf("Результат выстрела игрока %s: ", bot.getName());
                System.out.println(getMessageAfterShoot(botResult));
                if (botResult != ShootState.MISS) {
                    player.showField();
                    System.out.println("Ждем хода компьютера...");
                    Thread.currentThread().sleep(3000);
                }
            } while (botResult != ShootState.MISS);
        }
        if (player.isAlive()) {
            System.out.println("Игрок " + player.getName() + " победил!");
        } else if (bot.isAlive()) {
            System.out.println("Победу одержал искусственный интеллект.");
        } else {
            System.out.println("Удивительно, но случилась ничья. Все утонули и поздравлять некого :(");
        }
    }
    /*
    public static void game() throws IOException {
        while (bot.isAlive()) {
            ShootState result = requestPlayerToShoot();
            System.out.println(getMessageAfterShoot(result));
            player.showOwnShoots();
        }
    }
*/
    private static String getMessageAfterShoot(ShootState result) {
        String msg = null;
        switch (result) {
            case MISS:
                msg = "Промах";
                break;
            case REPEAT:
                msg = "Повторный выстрел в клетку.";
                break;
            case HURT:
                msg = "Ранен корабль противника.";
                break;
            case KILLED:
                msg = "Убит корабль противника.";
                break;
        }
        return msg;
    }

    private static void requestPlaceShip(int size) throws IOException {
        outer:
        while (true) {
            System.out.printf("Введите координату начала корабля размера %d " +
                    "в шахматном формате и направление через пробел " +
                    "u - up, d - down, r - right, l - left\n +" +
                    "Пример: a7 r или j2 d\n", size);
            player.showField();
            String input = reader.readLine().toLowerCase().replaceAll("\t+", " ");
            String turn = input.split(" ")[0];
            int x = turn.charAt(0) - 'a';
            int y = 0;
            try {
                y = Integer.parseInt(turn.substring(1)) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Неправильный формат ввода! Попробуйте еще раз");
                continue;
            }
            Directions dir;
            if (input.split(" ").length < 2) {
                System.out.println("Введите корректнй код направления.");
                continue;
            }
            switch (input.split(" ")[1]) {
                case "u":
                    dir = Directions.UP;
                    break;
                case "d":
                    dir = Directions.DOWN;
                    break;
                case "l":
                    dir = Directions.LEFT;
                    break;
                case "r":
                    dir = Directions.RIGHT;
                    break;
                default:
                    System.out.println("Введите корректный код направления");
                    continue outer;
            }
            boolean attempt = player.tryPlaceOneShip(x, y, size, dir);
            if (attempt) {
                System.out.printf("Корабль размера %d размещен успешно\n", size);
                player.showField();
                break;
            } else {
                System.out.println("Корабль такой конфигурации разместить невозможно!");
            }
        }
    }

    private static ShootState requestPlayerToShoot() throws IOException {
        while (true) {
            System.out.println("Введите координаты точки, куда вы хотите выстрелить, используя шахматный формат" +
                    "(Координаты варьирются от a1 до j10, буквы соответствуют горизонтальному направлению.)" );
            String turn = reader.readLine().toLowerCase();
            int x = turn.charAt(0) - 'a';
            int y = 0;
            try {
                y = Integer.parseInt(turn.substring(1)) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Неправильный формат ввода! Попробуйте еще раз");
                continue;
            }
            if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
                System.out.println("Неправильный формат ввода! Попробуйте еще раз");
                continue;
            }
            return player.shootEnemy(bot, x, y);
        }
    }

    public static void main(String[] args) throws Exception {
        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Введите ваше имя");
        player = new Player(reader.readLine());
        placeShips();
        game2();
    }


}
