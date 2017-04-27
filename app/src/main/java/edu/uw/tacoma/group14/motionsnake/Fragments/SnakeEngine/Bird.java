/*
 * Motion Snake Game
 *
 * Author: Justin Arnett
 * Version: May 4, 2016
 */

package edu.uw.tacoma.group14.motionsnake.Fragments.SnakeEngine;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.Random;

/**
 * Bird object in the game of Motion Snake. It is there to
 * be taken out by the snake entity for the player to
 * accumulate score.
 */
public class Bird {
    public static final int BIRD_RADIUS = 75;
    public static final int BIRD_SIZE = 150;
    int lifeTimer;
    int scoreSnapShot;
    Bitmap bmpBird;
    int x;
    int y;
    Point center;
    boolean showScore;
    int scoreX;
    int scoreY;
    int scoreTimer;

    /**
     * Constructor for the Bird Object.
     * @param img The image of the Bird
     */
    public Bird(Bitmap img) {
        lifeTimer = 0;
        scoreSnapShot = 0;
        showScore = false;
        scoreX = 0;
        scoreY = 0;
        scoreTimer = 0;
        bmpBird = img;
        bmpBird = Bitmap.createScaledBitmap(bmpBird, BIRD_SIZE, BIRD_SIZE, false);
        this.x = 0;
        this.y = 0;
        center = new Point((BIRD_SIZE / 2), (BIRD_SIZE / 2));
    }

    public int getScore() {
        scoreSnapShot = (int) ((Math.log10(lifeTimer + 100)) / (Math.log10(0.991))) + 1010;
        return scoreSnapShot;
    }

    /**
     * Creates a new Bird with random coordinates.
     *
     * @param maxW Width of the device's screen.
     * @param maxH Height of the device's screen.
     */
    public void spawnRandom(int maxW, int maxH) {
        scoreX = this.x;
        scoreY = this.y;
        lifeTimer = 0;
        Random rand = new Random();
        this.x = rand.nextInt((maxW - BIRD_SIZE) + 1);
        this.y = rand.nextInt((maxH - BIRD_SIZE) + 1);
        this.center.x = this.x + (BIRD_SIZE / 2);
        this.center.y = this.y + (BIRD_SIZE / 2);
    }

    /**
     * Checks if there was a collision from the Bird entity
     * with the provided circle values.
     *
     * @param theX x coordinate of the collision circle
     * @param theY y coordinate of the collision circle
     * @param theR Radius of the collision circle
     * @return Returns true if there was a collision, false otherwise.
     */
    public boolean collision(float theX, float theY, int theR) {
        double distance = Math.sqrt((theX - this.center.x)*(theX-this.center.x)
                + (theY - this.center.y)*(theY-this.center.y));
        return distance < (theR + BIRD_RADIUS);
    }

}