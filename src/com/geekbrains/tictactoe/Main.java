package com.geekbrains.tictactoe;

import java.util.Scanner;


public class Main {

    //Длина стороны игрового поля
    private static final int SIZE = 5;
    //Минимальная длина выигрышной диаганали
    private static final int MIN_DOTS_TO_WIN = 3;

    private static final char DOT_EMPTY = '•';
    private static final char DOT_X = 'X';
    private static final char DOT_O = 'O';

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final int DOWNWARD_DIAGONAL = 2;
    private static final int ASCENDING_DIAGONAL = 3;
    private static final int COUNT_DIRECTIONS = 4;

    private static char[][] map;
    private static int turnCount = 0;
    private static final Scanner scanner = new Scanner(System.in);
    private static int humanTurnX, humanTurnY;

    public static void main(String[] args) {
        initMap();
        do {
            printMap();
            humanTurn();
        } while (!checkWin(DOT_X, humanTurnX, humanTurnY));
        scanner.close();
    }

    /**
     * Инициализация массива игрового поля
     */
    private static void initMap() {
        map = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                map[i][j] = DOT_EMPTY;
            }
        }
    }

    /**
     * Отображает игровое поле
     */
    private static void printMap() {
        System.out.print("  ");
        for (int i = 1; i <= SIZE; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0; i < SIZE; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < SIZE; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Ввод координат хода игрока
     */
    private static void humanTurn() {
        do {
            System.out.println("Введите координаты в формате X Y");
            humanTurnX = scanner.nextInt() - 1;
            humanTurnY = scanner.nextInt() - 1;
        } while (!isCellValid());
        map[humanTurnY][humanTurnX] = DOT_X;
        turnCount++;
    }

    /**
     * Проверка координат хода игрока
     * @return <code>boolean</code>
     */
    private static boolean isCellValid() {
        if (humanTurnX < 0 || humanTurnX >= SIZE || humanTurnY < 0 || humanTurnY >= SIZE) return false;
        return map[humanTurnY][humanTurnX] == DOT_EMPTY;
    }


    /**
     * Проверка хода игрока на победу и ответный ход компьютера
     * @param symbol символ
     * @param x координата
     * @param y координата
     * @return <code>boolean</code>
     */
    private static boolean checkWin(char symbol, int x, int y) {
        //Находим начальную точку нисходящей диагонали содержащей точку
        int downwardDiagonalX = x - y;
        int downwardDiagonalY = 0;
        int downwardDiagonalLength = 0;
        if (downwardDiagonalX < 0) {
            downwardDiagonalY = Math.abs(downwardDiagonalX);
            downwardDiagonalX = 0;
        }
        //Находим начальную точку восходящей диагонали содержащей точку
        int ascendingDiagonalX = x - SIZE + y + 1;
        int ascendingDiagonalY = SIZE - 1;
        int ascendingDiagonalLength = 0;
        if (ascendingDiagonalX < 0) {
            ascendingDiagonalY += ascendingDiagonalX;
            ascendingDiagonalX = 0;
        }
        //Заполняем статистику по направлениям
        int[] statByDirections = new int[COUNT_DIRECTIONS];
        for (int i = 0; i < SIZE; i++) {
            if (map[y][i] == symbol) {
                statByDirections[HORIZONTAL]++;
            }
            if (map[i][x] == symbol) {
                statByDirections[VERTICAL]++;
            }
            if (downwardDiagonalX < SIZE && downwardDiagonalY < SIZE) {
                if (map[downwardDiagonalY][downwardDiagonalX] == symbol) {
                    statByDirections[DOWNWARD_DIAGONAL]++;
                }
                downwardDiagonalLength++;
                downwardDiagonalX++;
                downwardDiagonalY++;
            }
            if (ascendingDiagonalX < SIZE && ascendingDiagonalY > -1) {
                if (map[ascendingDiagonalY][ascendingDiagonalX] == symbol) {
                    statByDirections[ASCENDING_DIAGONAL]++;
                }
                ascendingDiagonalLength++;
                ascendingDiagonalX++;
                ascendingDiagonalY--;
            }
        }

        //Выигрышь
        if (statByDirections[HORIZONTAL] == SIZE
                || statByDirections[VERTICAL] == SIZE
                || (statByDirections[DOWNWARD_DIAGONAL] == downwardDiagonalLength && downwardDiagonalLength >= MIN_DOTS_TO_WIN)
                || (statByDirections[ASCENDING_DIAGONAL] == ascendingDiagonalLength && ascendingDiagonalLength >= MIN_DOTS_TO_WIN)) {
            printMap();
            String message = symbol == DOT_X ? "Вы выиграли" : "Компьютер выиграл";
            System.out.println(message);
            return true;
        }
        //Ничья
        if (turnCount == SIZE * SIZE) {
            printMap();
            System.out.println("Ничья");
            return true;
        }
        //Ход компьютера
        if (symbol != DOT_O) {
            // Ищем выигрышную комбинацию компьютера
            int[] c = checkWinningCombination(DOT_O);
            if (c != null) {
                map[c[0]][c[1]] = DOT_O;
                turnCount++;
                return checkWin(DOT_O, c[1], c[0]);
            }
            // Ищем выигрышную комбинацию игрока
            c = checkWinningCombination(DOT_X);
            if (c != null) {
                map[c[0]][c[1]] = DOT_O;
                turnCount++;
                return checkWin(DOT_O, c[1], c[0]);
            }
            // Блокируем комбинацию игрока
            boolean turn = false;
            do {
                int dir = 0;
                int max = statByDirections[0];
                boolean allDirEquals = true;
                for (int i = 1; i < COUNT_DIRECTIONS; i++) {
                    if (statByDirections[i] > max) {
                        max = statByDirections[i];
                        dir = i;
                    }
                    if (statByDirections[i - 1] != statByDirections[i]) {
                        allDirEquals = false;
                    }
                }
                if (allDirEquals) {
                    dir = downwardDiagonalLength > ascendingDiagonalLength ? DOWNWARD_DIAGONAL : ASCENDING_DIAGONAL;
                }
                int dx = downwardDiagonalX - 1;
                int dy = downwardDiagonalY - 1;
                int ax = ascendingDiagonalX - 1;
                int ay = ascendingDiagonalY + 1;

                for (int i = 0; i < SIZE; i++) {
                    if (dir == HORIZONTAL && map[y][i] == DOT_EMPTY) {
                        x = i;
                        turn = true;
                        break;
                    } else if (dir == VERTICAL && map[i][x] == DOT_EMPTY) {
                        y = i;
                        turn = true;
                        break;
                    } else if (dir == DOWNWARD_DIAGONAL && map[dy][dx] == DOT_EMPTY) {
                        x = dx;
                        y = dy;
                        turn = true;
                        break;
                    } else if (dir == ASCENDING_DIAGONAL && map[ay][ax] == DOT_EMPTY) {
                        x = ax;
                        y = ay;
                        turn = true;
                        break;
                    }
                    if (dx > 0) {
                        dx--;
                    }
                    if (dy > 0) {
                        dy--;
                    }
                    if (ax > 0) {
                        ax--;
                    }
                    if (ay < SIZE - 1) {

                        ay++;
                    }
                }
                if (!turn) {
                    statByDirections[dir] = 0;
                }
            } while (!turn);
            map[y][x] = DOT_O;
            turnCount++;
            return checkWin(DOT_O, x, y);
        }
        return false;
    }

    /**
     * Сканирует игровое поле на наличие направлений в которых до выигрыша остается один ход
     * @param symbol символ
     * @return массив с координатами точки
     */
    private static int[] checkWinningCombination(char symbol) {
        for (int i = 0; i < SIZE; i++) {
            int dir = -1;
            int h = 0;
            int v = 0;
            int dd = 0;
            int ad = 0;
            int dl = 0;
            int dx = i;
            int dy = 0;
            for (int j = 0; j < SIZE; j++) {
                if (map[i][j] == symbol) {
                    h++;
                    if (h == SIZE - 1) {
                        dir = HORIZONTAL;
                        break;
                    }
                }
                if (map[j][i] == symbol) {
                    v++;
                    if (v == SIZE - 1) {
                        dir = VERTICAL;
                        break;
                    }
                }
                if (dx > -1 && dy < SIZE) {
                    dl++;
                    if (map[dy][dx] == symbol) {
                        dd++;
                    }
                    if (map[SIZE - dy - 1][dx] == symbol) {
                        ad++;
                    }
                }
                dx--;
                dy++;
            }
            if (dir < 0 && dl >= MIN_DOTS_TO_WIN) {
                if (dd == dl - 1) {
                    dir = DOWNWARD_DIAGONAL;
                } else if (ad == dl - 1) {
                    dir = ASCENDING_DIAGONAL;
                }
            }
            if (dir > -1) {
                dx = i;
                for (int j1 = 0; j1 < SIZE; j1++) {
                    if (dir == HORIZONTAL && map[i][j1] == DOT_EMPTY) {
                        return new int[]{i, j1};
                    }
                    if (dir == VERTICAL && map[j1][i] == DOT_EMPTY) {
                        return new int[]{j1, i};
                    }
                    if (dx >= 0) {
                        if (dir == DOWNWARD_DIAGONAL && map[j1][dx] == DOT_EMPTY) {
                            return new int[]{j1, dx};
                        }
                        if (dir == ASCENDING_DIAGONAL && map[SIZE - j1 - 1][dx] == DOT_EMPTY) {
                            return new int[]{SIZE - j1 - 1, dx};
                        }
                    }
                    dx--;
                }
            }
        }
        return null;
    }


}
