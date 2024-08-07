package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Controller {
    private static final int SQUARE_SIZE = 100;
    private static final int WIDTH = 3;
    private static final int HEIGHT = 3;
    private static final int FRAME_WIDTH = WIDTH * SQUARE_SIZE;
    private static final int FRAME_HEIGHT = HEIGHT * SQUARE_SIZE;
    private static final int MIN_WIN_NUMBER = 3;

    private Graphics graphics;
    private View view;
    private final Symbol[][] locations = new Symbol[WIDTH][HEIGHT];
    private boolean isCrossTurn = true;
    private Set<Point> setOfPoints = new HashSet<>();

    public void setView(View view) {
        this.view = view;
    }

    public void start() {
        view.create(FRAME_WIDTH, FRAME_HEIGHT);
        render();
    }

    private void render() {
        BufferedImage image = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
        graphics = image.getGraphics();
        drawField();
        redrawTheBoard();
        view.setImageLabel(image);
    }

    private void redrawTheBoard() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (locations[x][y] == Symbol.CROSS) {
                    draw(createCross(), x, y);
                } else if (locations[x][y] == Symbol.CIRCLE) {
                    draw(createCircle(), x, y);
                }
            }
        }
    }

    private BufferedImage createSquare(boolean isWin) {
        BufferedImage bufferedImage = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        if (isWin) {
            graphics.setColor(Color.GREEN);
        }
        graphics.drawRect(0, 0, SQUARE_SIZE - 1, SQUARE_SIZE - 1);
        return bufferedImage;
    }

    private void draw(BufferedImage bufferedImage, int x, int y) {
        graphics.drawImage(bufferedImage, x * SQUARE_SIZE, y * SQUARE_SIZE, null);
    }

    private void drawField() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                draw(createSquare(setOfPoints.contains(new Point(x, y))), x, y);
            }
        }
    }

    public void handleMouseClick(int mouseX, int mouseY) {
        if (!setOfPoints.isEmpty()) {
            return;
        }
        int x = mouseX / SQUARE_SIZE;
        int y = mouseY / SQUARE_SIZE;
        if (locations[x][y] != null) {
            return;
        }
        locations[x][y] = isCrossTurn ? Symbol.CROSS : Symbol.CIRCLE;
        isCrossTurn = !isCrossTurn;
        checkWinner();
        render();
    }

    //if (x + MIN_WIN_NUMBER > WIDTH) {
//        return;
//    }
    private void checkWinner() {
        for (int x = 0; x <= WIDTH - MIN_WIN_NUMBER; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                checkLineWin(x, y, 1, 0);
            }
        }
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y <= HEIGHT - MIN_WIN_NUMBER; y++) {
                checkLineWin(x, y, 0, 1);
            }
        }
        for (int x = 0; x <= WIDTH - MIN_WIN_NUMBER; x++) {
            for (int y = 0; y <= HEIGHT - MIN_WIN_NUMBER; y++) {
                checkLineWin(x, y, 1, 1);
            }
        }
        for (int x = MIN_WIN_NUMBER - 1; x < WIDTH; x++) {
            for (int y = 0; y <= HEIGHT - MIN_WIN_NUMBER; y++) {
                checkLineWin(x, y, -1, 1);
            }
        }
    }


    private void checkLineWin(int startX, int startY, int dX, int dY) {
        Symbol symbol = locations[startX][startY];
        if (symbol == null) {
            return;
        }
        for (int i = 1; i < MIN_WIN_NUMBER; i++) {
            if (locations[startX + i * dX][startY + i * dY] != symbol) {
                return;
            }
        }
        for (int i = 0; i < MIN_WIN_NUMBER; i++) {
            setOfPoints.add(new Point(startX + i * dX, startY + i * dY));
        }

//        boolean present = IntStream.range(1, MIN_WIN_NUMBER)
//                .mapToObj(i -> locations[startX + i * dX][startY + i * dY])
//                .allMatch(s -> s == symbol);
//        if (present) {
//            return;
//        }

        IntStream.range(0, MIN_WIN_NUMBER)
                .mapToObj(i -> new Point(startX + i * dX, startY + i * dY))
                .forEach(setOfPoints::add);
    }

//    private boolean isVerticalWin(int startX, int startY) {
//        Symbol symbol = locations[startX][startY];
//        if (symbol == null) {
//            return false;
//        }
//        for (int i = 1; i < MIN_WIN_NUMBER; i++) {
//            if (locations[startX + i][startY] != symbol) {
//                return false;
//            }
//        }
//        return true;
//    }

    private BufferedImage createCircle() {
        BufferedImage bufferedImage = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawOval(0, 0, SQUARE_SIZE, SQUARE_SIZE);
        return bufferedImage;
    }

    private BufferedImage createCross() {
        BufferedImage bufferedImage = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawLine(0, 0, SQUARE_SIZE, SQUARE_SIZE);
        graphics.drawLine(0, SQUARE_SIZE, SQUARE_SIZE, 0);
        return bufferedImage;
    }
}
