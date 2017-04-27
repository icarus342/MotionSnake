/*
 * Motion Snake Game
 *
 * Author: Justin Arnett
 * Version: May 4, 2016
 */

package edu.uw.tacoma.group14.motionsnake.Fragments.SnakeEngine;

import android.graphics.Bitmap;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Random;

/**
 * Snake entity used in the game Motion Snake. It contains
 * a snake head entity, snake neck, and snake body where the
 * body has the potential to grow in length.
 */
public class Snake {
    // Snake Head
    Bitmap bmpSnakeH;
    float xVelocity = 0;
    float yVelocity = 0;
    int direction = 0;
    float snakeX = 500;
    float snakeY = 500;
    PointF snakeCenter = new PointF(snakeX + (SNAKE_SIZE / 2), snakeY + (SNAKE_SIZE / 2));

    public static final int SNAKE_SIZE = 150;
    public static final int SNAKE_RADIUS = 55;
    public static final int SNAKE_SPACING = 80;

    double distanceCounter = SNAKE_SPACING;

    // Snake Neck
    Bitmap bmpSnakeBody;
    PointF neckCenter = new PointF(snakeCenter.x - SNAKE_SPACING, snakeCenter.y);
    float neckDestX = snakeCenter.x;
    float neckDestY = snakeCenter.y;
    float neckDistanceX = neckDestX - neckCenter.x;
    float neckDistanceY = neckDestY - neckCenter.y;

    ArrayList<SnakePiece> snakeBody;
    Bitmap bmpSnakeTail;


    /**
     * Constructor for a snake entity.
     *
     * @param head The image for the snake's head.
     */
    public Snake(Bitmap head, Bitmap body, Bitmap tail) {
        bmpSnakeH = head;
        bmpSnakeH = Bitmap.createScaledBitmap(bmpSnakeH, SNAKE_SIZE, SNAKE_SIZE, false);

        bmpSnakeBody = body;
        bmpSnakeBody = Bitmap.createScaledBitmap(bmpSnakeBody, SNAKE_SIZE, SNAKE_SIZE, false);

        bmpSnakeTail = tail;
        bmpSnakeTail = Bitmap.createScaledBitmap(bmpSnakeTail, SNAKE_SIZE, SNAKE_SIZE, false);

        snakeBody = new ArrayList<>();
        snakeBody.add(0, new SnakePiece(neckCenter.x - SNAKE_SPACING, neckCenter.y));
        snakeBody.add(1, new SnakePiece(snakeBody.get(0).center.x - SNAKE_SPACING, snakeBody.get(0).center.y));
        snakeBody.add(2, new SnakePiece(snakeBody.get(1).center.x - SNAKE_SPACING, snakeBody.get(1).center.y));

    }

    /**
     * Calculates the average velocity from the x velocity vector and
     * the y velocity vector.
     *
     * @return The overall speed (average velocity vector).
     */
    public double getOverallSpeed() {
        return Math.sqrt((xVelocity*xVelocity) + (yVelocity*yVelocity));
    }

    /**
     * Grows the snake by one Snake Piece. The last piece (the tail) will
     * move to the new last piece, and a new piece will be placed just
     * before the last piece.
     */
    public void grow() {
        //snakeBody.add(new SnakePiece(
        snakeBody.add(snakeBody.get(snakeBody.size()-1));
        snakeBody.set(snakeBody.size()-2, new SnakePiece(snakeBody.get(snakeBody.size()-3).center.x,
                snakeBody.get(snakeBody.size()-3).center.y));

        //snakeBody.get(snakeBody.size()-2).snakeNext(snakeBody.get(snakeBody.size()-3).center.x,
        //            snakeBody.get(snakeBody.size()-3).center.y);
        // Snake's tail
        //snakeBody.get(snakeBody.size()-1).snakeNext(snakeBody.get(snakeBody.size()-2).center.x,
        //            snakeBody.get(snakeBody.size()-2).center.y);
    }

    /**
     * Updates the coordinates of the snake's body to follow
     * each other.
     */
    public void update() {
        double speed = getOverallSpeed();
        distanceCounter -= speed;

        // If the distance traveled (speed) is greater than distance left
        if (distanceCounter >= speed) {
            moveSnake(speed);
        } else {
            // Extra should be negative or zero.
            double extra = speed - distanceCounter;

            // Move the remainder distance available.
            moveSnake(distanceCounter);

            // Update Neck
            neckDestX = snakeCenter.x;
            neckDestY = snakeCenter.y;
            neckDistanceX = neckDestX - neckCenter.x;
            neckDistanceY = neckDestY - neckCenter.y;

            // Update Body
            snakeBody.get(0).snakeNext(neckCenter.x, neckCenter.y);
            for (int i = 1; i < snakeBody.size(); i++) {
                snakeBody.get(i).snakeNext(snakeBody.get(i-1).center.x, snakeBody.get(i-1).center.y);
            }
            // Move snake the excess difference after updates.
            moveSnake(extra);
            distanceCounter = SNAKE_SPACING + extra;

        }
    }

    /**
     * Moves the Snake's neck and body by the given speed.
     *
     * @param speed The current speed of the snake's head.
     */
    private void moveSnake(double speed) {
        neckCenter.x += (speed / SNAKE_SPACING) * neckDistanceX;
        neckCenter.y += (speed / SNAKE_SPACING) * neckDistanceY;
        for (int i = 0; i < snakeBody.size(); i++) {
            snakeBody.get(i).center.x += (speed / SNAKE_SPACING) * snakeBody.get(i).distancex;
            snakeBody.get(i).center.y += (speed / SNAKE_SPACING) * snakeBody.get(i).distancey;
        }
    }


    /**
     * A Snake piece is a single segment of the snake's body.
     * Each segment follows the next one, leading up to the
     * snake's neck.
     */
    public class SnakePiece {
        float x;
        float y;
        PointF center;
        float destx;
        float desty;
        float distancex;
        float distancey;

        int deathx;
        int deathy;
        //int direction;

        /**
         * The constructor for a Snake Piece.
         *
         * @param theX The x coordinate of where the segment spawns.
         * @param theY The y coordinate of where the segment spawns.
         */
        public SnakePiece(float theX, float theY) {
            center = new PointF(theX, theY);
            Random rand = new Random();
            //this.x = rand.nextInt((maxW - BIRD_SIZE) + 1);
            deathx = rand.nextInt(9 + 8) - 8;
            deathy = rand.nextInt(9 + 8) - 8;
            //destx = x;
            //desty = y;
        }

        /**
         * Update the Snake Piece's destination coordinates
         * and distance to those coordinates.
         *
         * @param nextX The new x destination coordinate
         * @param nextY The new y destination coordinate
         */
        public void snakeNext(float nextX, float nextY) {
            destx = nextX;
            desty = nextY;
            distancex = destx - center.x;
            distancey = desty - center.y;
        }

        /**
         * Gets the image x location of this snakePiece
         *
         * @return the x location of the image.
         */
        public float getImageX() {
            return center.x - (Snake.SNAKE_SIZE / 2);
        }

        /**
         * Gets the image y location of this snakePiece
         *
         * @return the y location of the image.
         */
        public float getImageY() {
            return center.y - (Snake.SNAKE_SIZE / 2);
        }

        /**
         * get's the current direction of the snake body component.
         *
         * @return the angle direction.
         */
        public int getDirection() {
            return (int) (Math.toDegrees(Math.atan2(distancey, distancex)));

        }

        /**
         * The collision logic for the snake's body. Checks each
         * segment of the snake body for collision with the provided
         * values for a circle.
         *
         * @param theX The x coordinate of the snake head.
         * @param theY The y coordinate of the snake head.
         * @param theR The radius of the snake head.
         * @return Returns true if there was collision, false otherwise.
         */
        public boolean collision(float theX, float theY, int theR) {
            double distance = Math.sqrt((theX - this.center.x)*(theX-this.center.x)
                    + (theY - this.center.y)*(theY-this.center.y));

            return distance < (theR + SNAKE_RADIUS);
        }
    }
}