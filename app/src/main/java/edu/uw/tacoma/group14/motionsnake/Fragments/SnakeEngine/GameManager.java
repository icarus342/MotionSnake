/*
 * Motion Snake Game Manager. This handles operation of the game, which
 * includes updating entity positions, checking collision, and
 * drawing them to the view.
 *
 * Author: Justin Arnett
 * Version: May 5, 2016
 */


package edu.uw.tacoma.group14.motionsnake.Fragments.SnakeEngine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

import edu.uw.tacoma.group14.motionsnake.R;

/**
 * The Game Manager operates the game for Motion Snake. The run()
 * method is executed, running in an infinite loop to continuously
 * update the game. The game manager uses A snake entity and bird
 * entities for the game.
 */
class GameManager extends SurfaceView implements Runnable, SensorEventListener {

    public static final float MINIMUM_VELOCITY = 10;
    public static final float MAX_TURN_SPEED = 6; // in degrees per frame
    public static final int READY_PHASE_TIMER = 150;

    // Game Engine Variables
    Thread gameThread = null;
    SurfaceHolder ourHolder;
    volatile boolean stillPlaying;
    Context mContext;

    // Ready splash screen variables
    boolean readyScreen;
    boolean readyTiltForward;
    boolean readyTiltBack;
    boolean readyGetReady;
    boolean readyGo;

    // Settings
    String snakeColor;
    int axisAngle;
    boolean debugMode;

    // Rendering Variables
    Canvas canvas;
    Paint paint;
    long fps;
    boolean gameOver;

    // Device's Screen Size (screenSize.x and screenSize.y);
    private Point screenSize;

    // Gyroscope control variables
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float xAxisAcceleration;
    private float yAxisAcceleration;
    private float zAxisAcceleration;

    //Game Entities
    Snake mSnake;
    ArrayList<Bird> birdList = new ArrayList<>();
    Bitmap imageBird;
    int score = 0;

    //Bitmap imageBackground;
    Bitmap imageGrassTuft;
    Bitmap imageGrassPatch;
    ArrayList<Point> backgroundList = new ArrayList<>();

    /**
     * Constructor for the GameManager(game engine).
     *
     * @param ctx The Activity's context
     */
    public GameManager(Context ctx,int angle, String color, boolean debug) {
        super(ctx);
        axisAngle = angle;
        snakeColor = color;
        debugMode = debug;
        //loadFromSettingsDB();
        readyScreen = true;
        readyTiltForward = false;
        readyTiltBack = false;
        readyGetReady = false;
        readyGo = false;

        mContext = ctx;
        ourHolder = getHolder();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        gameOver = false;

        // instantiate Bitmap images
        screenSize = new Point();
        int headID = 0;
        int bodyID = 0;
        int tailID = 0;
        try {
            Class res = R.drawable.class;
            headID = res.getField(snakeColor + "_snake_head").getInt(null);
            bodyID = res.getField(snakeColor + "_snake_body").getInt(null);
            tailID = res.getField(snakeColor + "_snake_tail").getInt(null);
        }
        catch (Exception e) {
            Log.e("MyTag", "Failure to get drawable id.", e);
        }
        Bitmap imageSnakeHead = BitmapFactory.decodeResource(this.getResources(), headID);
        Bitmap imageSnakeBody = BitmapFactory.decodeResource(this.getResources(), bodyID);
        Bitmap imageSnakeTail = BitmapFactory.decodeResource(this.getResources(), tailID);
        mSnake = new Snake(imageSnakeHead, imageSnakeBody, imageSnakeTail);

        //imageBackground = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass_back_tile);
        //imageBackground = Bitmap.createScaledBitmap(imageBackground, 2440, 1320, false);
        imageGrassPatch = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass_patch);
        imageGrassTuft = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass_tuft);

        // Gravity Sensor Registration
        mSensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        } else {
            // The device does not have sensors that supports the Gravity API.
            Log.e("Error:", "Sensor not detected on device");
        }

        imageBird = BitmapFactory.decodeResource(this.getResources(), R.drawable.bird);
    }

    private void generateBackground() {
        int incWidth = screenSize.x / 4;
        int incHeight = screenSize.y / 3;
        Random rand = new Random();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {
                int tempX = rand.nextInt(incWidth) + (incWidth * c);
                int tempY = rand.nextInt(incHeight) + (incHeight * r);
                backgroundList.add(new Point(tempX, tempY));
            }
        }

    }

    /**
     * Game loop (update() and draw()).
     */
    @Override
    public void run() {
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getSize(screenSize);
        ((Activity)mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        generateBackground();
        fps = 60;

        // Walks player through set up of tilting phone
        // to zero out the x axis.
        update();
        readyScreen = true;
        boolean runningReadyCheckPhase = true;
        xAxisAcceleration = 2;
        while (runningReadyCheckPhase) {
            if (xAxisAcceleration >= 0.3) {
                readyTiltForward = true;
                readyTiltBack = false;
            } else if (xAxisAcceleration <= -0.3) {
                readyTiltForward = false;
                readyTiltBack = true;
            } else {
                readyTiltForward = false;
                readyTiltBack = false;
            }

            if (!readyTiltForward && !readyTiltBack) {
                runningReadyCheckPhase = false;
            }
            draw();
        }
        int counter = 0;
        readyGetReady = false;
        while (counter < READY_PHASE_TIMER) {
            counter++;
            if (counter < READY_PHASE_TIMER / 5 * 4) {
                readyGetReady = true;
            } else {
                readyGetReady = false;
                readyGo = true;
            }
            draw();
        }
        readyGetReady = false;
        readyGo = false;
        readyScreen = false;


        // Game's primary update/draw loop
        Looper.prepare();
        while (stillPlaying) {
            long startFrameTime = System.currentTimeMillis();

            // Update game's entity states (such as positions)
            update();
            // Draws the entities to the screen.
            draw();

            // Set fps factor based on how long device took
            // to run update() and draw();
            long frameLength = System.currentTimeMillis() - startFrameTime;
            if (frameLength > 0) {
                fps = 1000 / frameLength;
            }
        }
    }

    /**
     * The primary update method for the Game Manager. The update method
     * applies the gravity acceleration to the velocity of the snake, then
     * updates the position. It will also check for proper collision of the
     * snake's head with other entities.
     */
    public void update() {
        // Take the axis acceleration and apply it to the velocity, and change
        // the snake's position.
        mSnake.xVelocity = yAxisAcceleration * 4.0f;
        mSnake.yVelocity = xAxisAcceleration * 4.0f;

        forceMinimumSpeed();
        mSnake.direction = forceMaximumTurnSpeed();

        // Screen Border collision (assumes title bar and status bar are hidden).
        if (mSnake.snakeX + mSnake.xVelocity > screenSize.x - Snake.SNAKE_RADIUS * 2) {
            mSnake.snakeX = screenSize.x - Snake.SNAKE_RADIUS * 2;
            mSnake.xVelocity = 0;
            forceMinimumSpeed();
        } else if (mSnake.snakeX + mSnake.xVelocity < 0) {
            mSnake.snakeX = 0;
            mSnake.xVelocity = 0;
            forceMinimumSpeed();
        }
        if (mSnake.snakeY + mSnake.yVelocity < 0) {
            mSnake.snakeY = 0;
            mSnake.yVelocity = 0;
            forceMinimumSpeed();
        } else if (mSnake.snakeY + mSnake.yVelocity > screenSize.y - Snake.SNAKE_RADIUS * 2) {
            mSnake.snakeY = screenSize.y - Snake.SNAKE_RADIUS * 2;
            mSnake.yVelocity = 0;
            forceMinimumSpeed();
        }

        mSnake.snakeX += mSnake.xVelocity;
        mSnake.snakeCenter.x = (int) mSnake.snakeX + (Snake.SNAKE_SIZE / 2);

        mSnake.snakeY += mSnake.yVelocity;
        mSnake.snakeCenter.y = (int) mSnake.snakeY + (Snake.SNAKE_SIZE / 2);

        // Updates the snake's body
        mSnake.update();

        // Spawn new Bird if no bird is present
        if (birdList.size() < 2) {
            Bird temp = new Bird(imageBird);
            temp.spawnRandom(screenSize.x, screenSize.y);
            birdList.add(temp);
        }

        // Check for collision for snake's head with any birds on screen.
        for (Bird b : birdList) {
            if (b.collision(mSnake.snakeCenter.x, mSnake.snakeCenter.y, Snake.SNAKE_RADIUS)) {
                b.showScore = true;
                b.scoreTimer = 0;
                score += b.getScore();
                b.spawnRandom(screenSize.x, screenSize.y);
                mSnake.grow();
                b.lifeTimer = 0;
            }
            if (b.showScore) {
                b.scoreTimer++;
                if (b.scoreTimer > 80) {
                    b.showScore = false;
                }
            }
            b.lifeTimer++;
        }

        // Check for collision for snake's head with any part of the snake
        // body that isn't the neck. (neck and closest body part doesn't check).
        for (int i = 1; i < mSnake.snakeBody.size(); i++) {
            //Log.d("DEBUG", " Size:" + mSnake.snakeBody.size() + " Count:" + i);
            if (mSnake.snakeBody.get(i).collision(mSnake.snakeCenter.x, mSnake.snakeCenter.y, Snake.SNAKE_RADIUS)) {
                /* NOTE: Game Over logic will be added here to end the game. */
                //Log.d("DEBUG", "COLLISION");
                gameOver = true;
                killSnake();
                stillPlaying = false;
                //((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                i = mSnake.snakeBody.size();
                ((SnakeGame)mContext).score = score;
                ((SnakeGame)mContext).updateScore();
            } else {
                //gameOver = false;
            }
        }

    }

    /**
     * Force's the new angle the snake is trying to turn
     * to an angle within the threshold of the maximum
     * turn speed variable. The new angle is also detected
     * to be in the clockwise or counterclockwise direction.
     * If the new angle is already within the threshold,
     * just uses that angle.
     *
     * @return The new direction of the Snake.
     */
    private int forceMaximumTurnSpeed() {
        // The angle the snake's head is facing
        //double newDirection = (Math.toDegrees(Math.atan2(-1 * mSnake.xVelocity, mSnake.yVelocity)));
        double newDirection = (Math.toDegrees(Math.atan2(mSnake.yVelocity, mSnake.xVelocity)));

        // Check if the quadrants of the two angles cross
        // the threshold of -180 to 180.
        double currentDirection = mSnake.direction;
        double otherDirection = newDirection;
        if (currentDirection <= -90 && otherDirection >= 90) {
            currentDirection += 360;
        } else if (currentDirection >= 90 && otherDirection <= -90) {
            otherDirection += 360;
        }

        // Check if the angle difference is within the threshold of MAX_TURN_SPEED
        if (Math.abs(currentDirection - otherDirection) > MAX_TURN_SPEED) {

            double difference = newDirection - mSnake.direction;
            double moveVector = Math.sqrt(Math.pow(mSnake.yVelocity, 2) + Math.pow(mSnake.xVelocity, 2));

            if (difference <= -180.0)
                difference += 360.0;
            if (difference > 180.0)
                difference -= 360.0;

            if (difference > 0.0) {
                // turn max lefT
                newDirection = mSnake.direction + MAX_TURN_SPEED;
                if (newDirection < -180) // passed over angle limit
                    newDirection += 360;
            } else if (difference <= 0.0) {
                // turn max right
                newDirection = mSnake.direction - MAX_TURN_SPEED;
                if (newDirection >= 180) // passed over angle limit
                    newDirection -= 360;
            }
            mSnake.xVelocity = (float) (Math.cos(newDirection * Math.PI / 180) * moveVector);
            mSnake.yVelocity = (float) (Math.sin(newDirection * Math.PI / 180) * moveVector);
        }
        return (int) newDirection;
    }

    /**
     * Forces the snake to always move at least a specified
     * minimum speed. If snake is moving faster than minimum
     * speed, it continues its normal speed.
     */
    private void forceMinimumSpeed() {
        // Controls the minimum speed of the snake
        double moveVector = Math.sqrt(Math.pow(mSnake.yVelocity, 2) + Math.pow(mSnake.xVelocity, 2));
        if (moveVector != 0 && moveVector < MINIMUM_VELOCITY) {
            double percentBoost = MINIMUM_VELOCITY / moveVector;
            mSnake.xVelocity *= (float) percentBoost;
            mSnake.yVelocity *= (float) percentBoost;
        }
    }


    /**
     * Helper method that uses a matrix to help rotate the image.
     * NOTE: This is buggy and alternatives will be explored.
     *
     * @param img The image to be rotated.
     * @param angle The angle the image needs to be rotated to.
     * @return The image after it has been rotated.
     */
    private Bitmap getRotatedImage(Bitmap img, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        WeakReference<Bitmap> rotatedImg = new WeakReference<>(Bitmap.createBitmap(img,
                        0, 0,img.getWidth(), img.getHeight(), matrix, true));
        return rotatedImg.get();
    }


    /**
     * The primary draw method of the Game Manager. This will
     * take the information of entities updated from the update()
     * method, and "paint" them to the screen.
     */
    public void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();

            // Clears canvas without setting opaque color
//            Paint clearPaint = new Paint();
//            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//            canvas.drawPaint(clearPaint);
//            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            canvas.drawColor(Color.argb(255, 50, 120, 50));


            for (int i = 0; i < backgroundList.size(); i++) {
                //canvas.drawBitmap(imageGrassPatch, backgroundList.get(i).x, backgroundList.get(i).y, paint);
                canvas.drawBitmap(imageGrassTuft, backgroundList.get(i).x, backgroundList.get(i).y, paint);
            }
//            for (int r = 0; r < screenSize.y; r += 135) {
//                for (int c = 0; c < screenSize.x; c += 240) {
//                    canvas.drawBitmap(imageBackground, c, r, paint);
//                }
//            }

            // Debug text info
            if (debugMode) {
                paint.setColor(Color.argb(255, 249, 129, 0));
                paint.setTextSize(45);
                canvas.drawText("FPS:" + fps, 20, 40, paint);
                canvas.drawText("X Axis:" + xAxisAcceleration, 20, 80, paint);
                canvas.drawText("Y Axis:" + yAxisAcceleration, 20, 120, paint);
                canvas.drawText("Z Axis:" + zAxisAcceleration, 20, 160, paint);
                canvas.drawText("x:" + mSnake.snakeX, 20, 250, paint);
                canvas.drawText("y:" + mSnake.snakeY, 20, 290, paint);
                canvas.drawText("Size:" + mSnake.snakeBody.size(), 20, 330, paint);
                canvas.drawText("Angle:" + mSnake.direction, 20, 370, paint);
            }

            // Draw Tail before the body
            if (debugMode) {
                paint.setColor(Color.argb(255, 100, 255, 0));
                canvas.drawCircle(mSnake.snakeBody.get(mSnake.snakeBody.size() - 1).center.x,
                        mSnake.snakeBody.get(mSnake.snakeBody.size() - 1).center.y,
                        Snake.SNAKE_RADIUS, paint);
            } else {
                canvas.drawBitmap(getRotatedImage(mSnake.bmpSnakeTail, mSnake.snakeBody.get(mSnake.snakeBody.size() - 1).getDirection()), mSnake.snakeBody.get(mSnake.snakeBody.size() - 1).getImageX(), mSnake.snakeBody.get(mSnake.snakeBody.size() - 1).getImageY(), paint);
            }
            // Draw the snake's body in reverse order (so there is proper layering).

            for (int i = mSnake.snakeBody.size()-2; i >= 0; i--) {
                if (debugMode) {
                    canvas.drawCircle(mSnake.snakeBody.get(i).center.x,
                            mSnake.snakeBody.get(i).center.y,
                            Snake.SNAKE_RADIUS, paint);
                }
                canvas.drawBitmap(mSnake.bmpSnakeBody, mSnake.snakeBody.get(i).getImageX(), mSnake.snakeBody.get(i).getImageY(), paint);

            }

            // Draw the neck of the snake (connects the head and body).
            paint.setColor(Color.argb(255, 147, 196, 245));
            if (debugMode) {
                canvas.drawCircle(mSnake.neckCenter.x, mSnake.neckCenter.y, Snake.SNAKE_RADIUS, paint);
            }
            canvas.drawBitmap(mSnake.bmpSnakeBody, mSnake.neckCenter.x - (Snake.SNAKE_SIZE/2), mSnake.neckCenter.y - (Snake.SNAKE_SIZE/2), paint);


            // Draw the head of the snake.
            paint.setColor(Color.argb(255, 249, 129, 0));
            if (debugMode) {
                canvas.drawCircle(mSnake.snakeCenter.x, mSnake.snakeCenter.y, Snake.SNAKE_RADIUS, paint);
            }
            canvas.drawBitmap(getRotatedImage(mSnake.bmpSnakeH, mSnake.direction), mSnake.snakeX, mSnake.snakeY, paint);

            // Draw all the bird entities in the list
            for (Bird b : birdList) {
                if (debugMode) {
                    canvas.drawCircle(b.center.x, b.center.y, Bird.BIRD_RADIUS, paint);
                }
                canvas.drawBitmap(b.bmpBird, b.x, b.y, paint);
                if (b.showScore && !gameOver) {
                    canvas.drawText(""+b.scoreSnapShot, b.scoreX, b.scoreY+50-(b.scoreTimer), paint);
                }
            }

            // Draw the player's score to the screen.
            paint.setTextSize(90);
            canvas.drawText("Score:" + score,
                    screenSize.x - 700, 80, paint);

            // Draw game over message to the screen if the player triggers a game over.
            if (gameOver) {
                paint.setColor(Color.argb(255, 255, 64, 64));
                paint.setTextSize(200);
                canvas.drawText("GAME OVER", 800, 800, paint);
            }

            if (readyScreen) {
                drawReadySplash(canvas, paint);
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * The helper method for handling display messages
     * for the ready splash screen before the game starts.
     * Helps the user orient the device to properly zero
     * the x-axis for proper controls of the snake. Once ready,
     * the game gets the user ready for the game starting.
     *
     * @param ctx The context of the activity
     * @param pt The paint on the canvas.
     */
    private void drawReadySplash(Canvas ctx, Paint pt) {
        pt.setColor(Color.argb(255, 255, 64, 64));
        pt.setTextSize(200);
        if (readyTiltForward) {
            ctx.drawText("TILT FORWARD", 800, 800, paint);
        } else if (readyTiltBack) {
            ctx.drawText("TILT BACK", 800, 800, paint);
        } else if (readyGetReady) {
            ctx.drawText("GET READY!", 800, 800, paint);
        } else if (readyGo) {
            ctx.drawText("GO!", 800, 800, paint);
        }

    }


    /**
     * pause() is called from outside the GameManager
     * when onPause() is called. Stops the thread
     * from running and halts the Game Manager's
     * update/draw loop.
     */
    public void pause() {
        stillPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
        mSensorManager.unregisterListener(this);
    }

    /**
     * resume() is called from outside the GameManager
     * when onResume() is called. Recreates the game's thread,
     * then enables the Game Manager's update/draw loop.
     */
    public void resume() {

        stillPlaying = true;

        gameThread = new Thread(this);
        gameThread.start();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }


    /**
     * This method is called when the accuracy of the sensor
     * changes its state of reliability. Not needed for this game,
     * but needs to be overridden.
     *
     * @param sensor The sensor
     * @param accuracy The current state of accuracy of the sensor
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * When the sensor detects a change, it will call this method.
     * The acceleration used will be updated based off the values
     * from the sensors.
     *
     * @param theEvent Contains information about the Sensor's data.
     */
    @Override
    public void onSensorChanged(SensorEvent theEvent) {
        xAxisAcceleration = theEvent.values[0] - axisAngle;
        yAxisAcceleration = theEvent.values[1];
        zAxisAcceleration = theEvent.values[2];
    }

    /**
     * Skeleton of method for touch screen controls. May be developed
     * to provide an alternative way to play Motion Snake as well as
     * use an in game menu.
     *
     * @param theEvent Information about touch event.
     * @return If the touch event was successful.
     */
    @Override
    public boolean onTouchEvent(MotionEvent theEvent) {
//        switch (theEvent.getAction() & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN:
//                mSnake.isMoving = true;
//                break;
//
//            case MotionEvent.ACTION_UP:
//                mSnake.isMoving = false;
//                break;
//        }
        return true;
    }
//
//    /**
//     * Loads settings from SQLite local database.
//     */
//    private void loadFromSettingsDB() {
//        //snakeColor = "green";
//        //axisAngle = 0;
//        //debugMode = false;
//
//        SettingDB settingDB = new SettingDB(mContext);
//        String[] strings = settingDB.getSetting().split(",");
//        axisAngle = Integer.parseInt(strings[0]);
//        snakeColor = strings[1];
//        debugMode = (Integer.parseInt(strings[2]) == 1)? true : false;
//    }

    /**
     * The custom loop that executes after a game over. The snake's
     * body cells will move in their respective directions for
     * a specified length of time.
     */
    private void killSnake() {
        int counter = 0;
        while (counter < 250) {
            for (int i = 0; i < mSnake.snakeBody.size(); i++) {
                mSnake.snakeBody.get(i).center.x += mSnake.snakeBody.get(i).deathx;
                mSnake.snakeBody.get(i).center.y += mSnake.snakeBody.get(i).deathy;
            }
            draw();
            counter++;
        }
    }

}