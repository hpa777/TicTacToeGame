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
        Main main = new Main();
        initMap();
        do {
            printMap();
            humanTurn();
        } while (!main.checkWin(DOT_X, humanTurnX, humanTurnY));
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
     *
     * @return <code>boolean</code>
     */
    private static boolean isCellValid() {
        if (humanTurnX < 0 || humanTurnX >= SIZE || humanTurnY < 0 || humanTurnY >= SIZE) return false;
        return map[humanTurnY][humanTurnX] == DOT_EMPTY;
    }

    /**
     * Класс содержащий информацию о векторе игрового поля.
     */
    private class Vector {

        private int vectorDirection;

        private int vectorLength = 0;

        public int xCount = 0;

        private int oCount = 0;

        public int emptyX = -1;
        
        public int emptyY = -1;

        public Vector(int vectorDirection) {
            this.vectorDirection = vectorDirection;
        }

        /**
         * Вектор содержит пустую точку
         * @return <code>boolean</code>
         */
        public boolean hasEmptyPoint() {
            return this.emptyX >= 0 && this.emptyY >= 0;
        }

        /**
         * Добавляем точку ветора
         * @param symbol
         * @param x
         * @param y
         */
        public void addSymbol(char symbol, int x, int y) {
            switch (symbol) {
                case DOT_X:
                   this.xCount++;
                   break;
                case DOT_O:
                    this.oCount++;
                    break;
                case DOT_EMPTY:
                    this.emptyX = x;
                    this.emptyY = y;
                    break;
            }
            this.vectorLength++;
        }

        /**
         * Проверяем вектор на "победу"
         * @param symbol
         * @return
         */
        public boolean checkWinBySymbol(char symbol) {
            if (this.vectorDirection == HORIZONTAL || this.vectorDirection == VERTICAL) {
                if (symbol == DOT_X && this.xCount == this.vectorLength) {
                    return true;
                }
                if (symbol == DOT_O && this.oCount == this.vectorLength) {
                    return true;
                }
            } else {
                if (symbol == DOT_X && this.xCount == this.vectorLength && this.vectorLength >= MIN_DOTS_TO_WIN) {
                    return true;
                }
                if (symbol == DOT_O && this.oCount == this.vectorLength && this.vectorLength >= MIN_DOTS_TO_WIN) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Проверяем вектор на потенциальную победу (один ход до победы)
         * @param symbol
         * @return
         */
        public boolean checkWinningCombination(char symbol) {
            if (this.vectorDirection == HORIZONTAL || this.vectorDirection == VERTICAL) {
                if (symbol == DOT_X && this.oCount == 0 && this.xCount == this.vectorLength - 1) {
                    return true;
                }
                if (symbol == DOT_O && this.xCount == 0 && this.oCount == this.vectorLength - 1) {
                    return true;
                }
            } else {
                if (symbol == DOT_X && this.oCount == 0 && this.xCount == this.vectorLength - 1 && this.vectorLength >= MIN_DOTS_TO_WIN) {
                    return true;
                }
                if (symbol == DOT_O && this.xCount == 0 && this.oCount == this.vectorLength - 1 && this.vectorLength >= MIN_DOTS_TO_WIN) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Проверка хода игрока на победу и ответный ход компьютера
     *
     * @param symbol символ
     * @param x      координата
     * @param y      координата
     * @return <code>boolean</code>
     */
    private boolean checkWin(char symbol, int x, int y) {
        //Находим начальную точку нисходящей диагонали содержащей точку
        int downwardDiagonalX = x - y;
        int downwardDiagonalY = 0;
        if (downwardDiagonalX < 0) {
            downwardDiagonalY = Math.abs(downwardDiagonalX);
            downwardDiagonalX = 0;
        }
        //Находим начальную точку восходящей диагонали содержащей точку
        int ascendingDiagonalX = x - SIZE + y + 1;
        int ascendingDiagonalY = SIZE - 1;
        if (ascendingDiagonalX < 0) {
            ascendingDiagonalY += ascendingDiagonalX;
            ascendingDiagonalX = 0;
        }
        //Заполняем статистику по направлениям
        Vector[] statByDirections = new Vector[COUNT_DIRECTIONS];
        for (int i = 0; i < COUNT_DIRECTIONS; i++) {
            statByDirections[i] = new Vector(i);
        }        
        for (int i = 0; i < SIZE; i++) {
            statByDirections[HORIZONTAL].addSymbol(map[y][i], i, y);
            statByDirections[VERTICAL].addSymbol(map[i][x], x, i);            
            if (downwardDiagonalX < SIZE && downwardDiagonalY < SIZE) {
                statByDirections[DOWNWARD_DIAGONAL].addSymbol(map[downwardDiagonalY][downwardDiagonalX], downwardDiagonalX, downwardDiagonalY); 
                downwardDiagonalX++;
                downwardDiagonalY++;
            }
            if (ascendingDiagonalX < SIZE && ascendingDiagonalY > -1) {
                statByDirections[ASCENDING_DIAGONAL].addSymbol(map[ascendingDiagonalY][ascendingDiagonalX], ascendingDiagonalX, ascendingDiagonalY);
                ascendingDiagonalX++;
                ascendingDiagonalY--;
            }
        }

        //Выигрышь
        for (int i = 0; i < COUNT_DIRECTIONS; i++) {
            if (statByDirections[i].checkWinBySymbol(symbol)) {
                printMap();
                String message = symbol == DOT_X ? "Вы выиграли" : "Компьютер выиграл";
                System.out.println(message);
                return true;
            }
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
            for (Vector vector :
                    statByDirections) {
                if (vector.checkWinningCombination(DOT_X)) {
                    map[vector.emptyY][vector.emptyX] = DOT_O;
                    turnCount++;
                    return checkWin(DOT_O, vector.emptyX, vector.emptyY);
                }
            }
            // Блокируем комбинацию игрока
            int dir = 0;
            int max = -1;
            boolean allDirEquals = true;
            for (int i = 0; i < COUNT_DIRECTIONS; i++) {
                if (!statByDirections[i].hasEmptyPoint()) {
                    continue;
                } else if (max < 0) {
                    max = statByDirections[i].xCount;
                    dir = i;
                } else if (statByDirections[i].xCount > max) {
                    max = statByDirections[i].xCount;
                    dir = i;
                    allDirEquals = false;
                }
            }
            if (allDirEquals) {
                if (statByDirections[DOWNWARD_DIAGONAL].hasEmptyPoint()) {
                    dir = DOWNWARD_DIAGONAL;
                } else if (statByDirections[ASCENDING_DIAGONAL].hasEmptyPoint()) {
                    dir = ASCENDING_DIAGONAL;
                } else if (statByDirections[HORIZONTAL].hasEmptyPoint()) {
                    dir = HORIZONTAL;
                } else  if (statByDirections[VERTICAL].hasEmptyPoint()) {
                    dir = VERTICAL;
                }
            }
            Vector vector = statByDirections[dir];
            map[vector.emptyY][vector.emptyX] = DOT_O;
            turnCount++;
            return checkWin(DOT_O, vector.emptyX, vector.emptyY);
        }
        return false;
    }

    /**
     * Сканирует игровое поле на наличие направлений в которых до выигрыша остается один ход
     *
     * @param symbol символ
     * @return массив с координатами точки
     */
    private static int[] checkWinningCombination(char symbol) {
        char invertSymbol = symbol == DOT_X ? DOT_O : DOT_X;
        for (int i = 0; i < SIZE; i++) {
            int dir = -1;

            int h = 0;
            int[] hArr = null;

            int v = 0;
            int[] vArr = null;

            int dd = 0;
            int[] ddArr = null;

            int ddp = 0;
            int[] ddpArr = null;

            int ad = 0;
            int[] adArr = null;

            int adp = 0;
            int[] adpArr = null;

            int dl = 0;
            int dx = i;
            int dy = 0;

            for (int j = 0; j < SIZE; j++) {
                //Скан горизонтали
                if (map[i][j] == symbol) {
                    h++;
                    if (h == SIZE - 1) {
                        dir = HORIZONTAL;
                    }
                } else if (map[i][j] == invertSymbol) {
                    h -= SIZE;
                    dir = -1;
                } else if (map[i][j] == DOT_EMPTY) {
                    hArr = new int[] {i, j};
                }
                //Скан вертикали
                if (map[j][i] == symbol) {
                    v++;
                    if (v == SIZE - 1) {
                        dir = VERTICAL;
                    }
                } else if (map[j][i] == invertSymbol) {
                    v -= SIZE;
                    dir = -1;
                } else if (map[j][i] == DOT_EMPTY) {
                    vArr = new int[] {j, i};
                }
                //Скан диагоналей
                if (dx > -1 && dy < SIZE) {
                    dl++;
                    if (map[dy][dx] == symbol) {
                        dd++;
                    } else if (map[dy][dx] == invertSymbol) {
                        dd -= SIZE;
                    } else if (map[dy][dx] == DOT_EMPTY) {
                        ddArr = new int[] {dy, dx};
                    }
                    if (map[SIZE - dx - 1][SIZE - dy - 1] == symbol) {
                        ddp++;
                    } else if (map[SIZE - dx - 1][SIZE - dy - 1] == invertSymbol) {
                        ddp -= SIZE;
                    } else if (map[SIZE - dx - 1][SIZE - dy - 1] == DOT_EMPTY) {
                        ddpArr = new int[] {SIZE - dx - 1, SIZE - dy - 1};
                    }
                    if (map[SIZE - dy - 1][dx] == symbol) {
                        ad++;
                    } else if (map[SIZE - dy - 1][dx] == invertSymbol) {
                        ad -= SIZE;
                    } else if (map[SIZE - dy - 1][dx] == DOT_EMPTY) {
                        adArr = new int[] {SIZE - dy - 1, dx};
                    }
                    if (map[dx][SIZE - dy - 1] == symbol) {
                        adp++;
                    } else if (map[dx][SIZE - dy - 1] == invertSymbol) {
                        adp -= SIZE;
                    } else if (map[dx][SIZE - dy - 1] == DOT_EMPTY) {
                        adpArr = new int[] {dx, SIZE - dy - 1};
                    }
                }
                dx--;
                dy++;
            }
            if (dir == HORIZONTAL) {
                return hArr;
            } else if (dir == VERTICAL) {
                return vArr;
            } else if (dl >= MIN_DOTS_TO_WIN) {
                if (dd == dl - 1) {
                    return ddArr;
                } else if (ad == dl - 1) {
                    return adArr;
                } else if (ddp == dl - 1) {
                    return ddpArr;
                } else if (adp == dl - 1) {
                    return adpArr;
                }
            }
        }
        return null;
    }

}