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

        public void turnToEmpty(char symbol) {
            if (this.hasEmptyPoint()) {
                map[emptyY][emptyX] = symbol;
                turnCount++;
            }
        }

        /**
         * Добавляем точку вектора
         * @param x
         * @param y
         */
        public void addSymbol(int x, int y) {
            switch (map[y][x]) {
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
            statByDirections[HORIZONTAL].addSymbol(i, y);
            statByDirections[VERTICAL].addSymbol(x, i);
            if (downwardDiagonalX < SIZE && downwardDiagonalY < SIZE) {
                statByDirections[DOWNWARD_DIAGONAL].addSymbol(downwardDiagonalX, downwardDiagonalY);
                downwardDiagonalX++;
                downwardDiagonalY++;
            }
            if (ascendingDiagonalX < SIZE && ascendingDiagonalY > -1) {
                statByDirections[ASCENDING_DIAGONAL].addSymbol(ascendingDiagonalX, ascendingDiagonalY);
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
            {
                Vector vector = this.searchWinCombination(DOT_O);
                if (vector != null) {
                    vector.turnToEmpty(DOT_O);
                    return checkWin(DOT_O, vector.emptyX, vector.emptyY);
                }
            }
            // Ищем выигрышную комбинацию игрока
            for (Vector vector :
                    statByDirections) {
                if (vector.checkWinningCombination(DOT_X)) {
                    vector.turnToEmpty(DOT_O);
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
            vector.turnToEmpty(DOT_O);
            return checkWin(DOT_O, vector.emptyX, vector.emptyY);
        }
        return false;
    }

    /**
     * Сканирует игровое поле на наличие направлений в которых до выигрыша остается один ход
     * @param symbol
     * @return Vector
     */
    private Vector searchWinCombination(char symbol) {
        for (int i = 0; i < SIZE; i++) {
            Vector horizontalVector = new Vector(HORIZONTAL);
            Vector verticalVector = new Vector(VERTICAL);
            Vector downwardDiagonalVector = new Vector(DOWNWARD_DIAGONAL);
            Vector ascendingDiagonalVector = new Vector(ASCENDING_DIAGONAL);
            Vector downwardDiagonalLeftVector = new Vector(DOWNWARD_DIAGONAL);
            Vector ascendingDiagonalLeftVector = new Vector(ASCENDING_DIAGONAL);
            int dx = i;
            int dy = 0;
            for (int j = 0; j < SIZE; j++) {
                horizontalVector.addSymbol(j, i);
                verticalVector.addSymbol(i, j);
                if (dx > -1 && dy < SIZE) {
                    downwardDiagonalVector.addSymbol(dx, dy);
                    downwardDiagonalLeftVector.addSymbol(SIZE - dy - 1, SIZE - dx - 1);
                    ascendingDiagonalVector.addSymbol(dx, SIZE - dy - 1);
                    ascendingDiagonalLeftVector.addSymbol(SIZE - dy - 1, dx);
                }
                dx--;
                dy++;
            }
            if (horizontalVector.checkWinningCombination(symbol)) {
                return horizontalVector;
            }
            if (verticalVector.checkWinningCombination(symbol)) {
                return verticalVector;
            }
            if (downwardDiagonalVector.checkWinningCombination(symbol)) {
                return downwardDiagonalVector;
            }
            if (downwardDiagonalLeftVector.checkWinningCombination(symbol)) {
                return downwardDiagonalLeftVector;
            }
            if (ascendingDiagonalVector.checkWinningCombination(symbol)) {
                return ascendingDiagonalVector;
            }
            if (ascendingDiagonalLeftVector.checkWinningCombination(symbol)) {
                return ascendingDiagonalLeftVector;
            }
        }
        return null;
    }

}