package com.example.ex1.Logic;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.example.ex1.Models.Driver;
import com.example.ex1.Models.MyTimer;
import com.example.ex1.Models.Obstacle;
import com.example.ex1.Models.ObstacleListener;
import com.example.ex1.Models.Position;
import com.example.ex1.Models.TimerCallback;
import com.example.ex1.Models.laneEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameManager
{
    private final int PER_SECOND_SCORE = 2;

    private double speedx;
    private double speedy;

    private boolean isStarted;

    private static final ReentrantLock obstacleListLock = new ReentrantLock ();
    private final Position leftLane = new Position(10, 10);
    private final Position midLane = new Position(400, 10);
    private final Position righttLane = new Position(800, 10);

    private final Map<laneEnum, Position> lanes = Map.of(
            laneEnum.right, righttLane,
            laneEnum.middle, midLane,
            laneEnum.left, leftLane
    );



    private ArrayList<ObstacleListener> obstacleListeners;


    private int obstacleImageResource;

    private int score;
    private int deaths;
    private int initial_lifes;

    private MyTimer timerAddBlock;
    private MyTimer timerMoveBlock;

    private ArrayList<Obstacle> obstacles;
    private Driver driver;

    public double getSpeedx() {
        return speedx;
    }

    public GameManager setSpeedx(double speedx) {
        this.speedx = speedx;
        return this;
    }

    public double getSpeedy() {
        return speedy;
    }

    public GameManager setSpeedy(double speedy) {
        this.speedy = speedy;
        return this;
    }

    public int getObstacleImageResource() {
        return obstacleImageResource;
    }

    public GameManager setObstacleImageResource(int obstacleImageResource) {
        this.obstacleImageResource = obstacleImageResource;
        return this;
    }

    public GameManager setDriverPos(Position driverpos)
    {
        this.driver.setLane(laneEnum.middle).setPosition(driverpos);
        return this;
    }
    public GameManager(int life) {
        this.initial_lifes = life;
        this.score = 0;
        this.deaths = 0;
        this.isStarted = false;
        this.driver = new Driver();
        TimerCallback callback1 = new TimerCallback() {
            @Override
            public void OnTick(long milisUntilFinished) {
                CreateBlock(milisUntilFinished);
            }

            @Override
            public void OnTimeout() {
                OnTimeoutCreateBlock();
            }


        };
        TimerCallback callback2 = new TimerCallback() {
            @Override
            public void OnTick(long milisUntilFinished) {
                MoveBlocks(milisUntilFinished);
            }

            @Override
            public void OnTimeout() {
                OnTimeoutMoveBlock();
            }


        };
        this.timerAddBlock = new MyTimer(60000000, 5, callback1);
        this.timerMoveBlock = new MyTimer(60000000, 1, callback2);
        this.obstacles = new ArrayList<>();
        this.obstacleListeners = new ArrayList<>();
    }


    public void moveDriverRight()
    {
        if (driver.getLane() == laneEnum.right)
            return;
        else if (driver.getLane() == laneEnum.left)
            moveDriver(laneEnum.middle);
        else if (driver.getLane() == laneEnum.middle)
            moveDriver(laneEnum.right);
    }
    public void moveDriverLeft()
    {
        if (driver.getLane() == laneEnum.left)
            return;
        else if (driver.getLane() == laneEnum.middle)
            moveDriver(laneEnum.left);
        else if (driver.getLane() == laneEnum.right)
            moveDriver(laneEnum.middle);
    }
    private void moveDriver(laneEnum lane)
    {
        driver.setPosition(new Position(lanes.get(lane).getTopleftx(),
                driver.getPosition().getToplefty()));
        driver.setLane(lane);
        for (ObstacleListener lis:
                this.obstacleListeners) {
            lis.driverMoved(driver.getPosition());
        }

    }

    public int getScore() {
        return score;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getInitial_lifes() {
        return initial_lifes;
    }

    public boolean isGameEnded() {
        return initial_lifes == deaths;
    }

    // todo
    public void checkColision() {
        Obstacle obsHit = null;
        for (Obstacle obs: this.obstacles ) {
            if(obs.getLane()!=driver.getLane()) continue;
            if(Math.abs(obs.getPosition().getToplefty() - driver.getPosition().getToplefty()) < 100)
            {
                obsHit = obs;
                break;
            }
        }

        this.removeBlock(obsHit);

        if (obsHit == null)
        {
            score += PER_SECOND_SCORE;

        } else { // incorrect
            // todo: move to activity. use event listener
            deaths++;
            for (ObstacleListener lis:
                 this.obstacleListeners) {
                lis.ObstacleHit(obsHit);
            }

        }
    }

    private void discardTimers()
    {
        timerAddBlock.discardTimer();
        timerMoveBlock.discardTimer();
    }
    private void restartTimers()
    {
        Log.d("lyf", "restartTimers: ");
        timerAddBlock.startTimer();
        timerMoveBlock.startTimer();
    }
    public void gameStart()
    {
        Log.d("lyf", "gameStart: ");
        this.isStarted = true;
        restartTimers();

    }
    public void gamePause()
    {
        if (this.isStarted)
            discardTimers();
    }
    public void gameResume()
    {
        if (this.isStarted)
            restartTimers();
    }
    public void gameDestroy()
    {
        if (this.isStarted)
            discardTimers();
    }

    public void removeBlock(Obstacle obs)
    {
        Log.d("remove", "removeBlock: ");
        this.obstacles.remove(obs);
        for (ObstacleListener lis:
                this.obstacleListeners) {
            lis.ObstacleRemoved(obs);
        }
    }

    public void MoveBlocks(long milisUntilFinished)
    {
        obstacleListLock.lock();
        // moving obstacle might remove them by view, if leave screen
        // so using a city list, to not change original mid loop
        ArrayList<Obstacle> copylist = new ArrayList<>(this.obstacles);
        for (Obstacle obs: copylist) {
            obs.move(speedx, speedy);
            obstacleListeners.forEach((el) -> el.ObstacleMoved(obs));
        }
        obstacleListLock.unlock();
        checkColision();
    }

    public void CreateBlock(long milisUntilFinished)
    {
        Random seed = new Random();

        ArrayList<laneEnum> keyList = new ArrayList<>(lanes.keySet());
        int ind = seed.nextInt(keyList.size());
        laneEnum randomLane = keyList.get(ind);
        Position pos = lanes.get(randomLane);

        Obstacle obs = new Obstacle().setImageResource(obstacleImageResource).setPosition(
                new Position(pos.getTopleftx(), pos.getToplefty())).
                setLane(randomLane);
        obstacleListLock.lock();
        this.obstacles.add(obs);
        obstacleListLock.unlock();

        obstacleListeners.forEach((el) -> el.ObstacleCreated(obs));

    }

    public void OnTimeoutCreateBlock() {
        Log.d("timer", "OnTimeout: timerCreateBlock finished. discarding timer.");
        timerAddBlock.discardTimer();
    }
    public void OnTimeoutMoveBlock() {
        Log.d("timer", "OnTimeout: timerMoveBlock finished. discarding timer.");
        timerMoveBlock.discardTimer();
    }

    public void addObstacleListener(ObstacleListener listener)
    {
        obstacleListeners.add(listener);
    }
}
