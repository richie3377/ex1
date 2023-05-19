package com.example.ex1.Logic;

import android.util.Log;

import com.example.ex1.Models.Driver;
import com.example.ex1.Models.MyTimer;
import com.example.ex1.Models.Obstacle;
import com.example.ex1.Models.ObstacleListener;
import com.example.ex1.Models.TimerCallback;

import java.util.ArrayList;
import java.util.Random;

public class GameManager
{
    private final int PER_SECOND_SCORE = 2;

    private boolean isStarted;

    private final ArrayList<ObstacleListener> obstacleListeners;

    private int speedObstacle;

    private int obstacleImageResource;

    private int score;
    private int deaths;
    private final int initial_lifes;

    private MyTimer timerAddBlock;
    private MyTimer timerMoveBlock;

    private final ArrayList<Obstacle> obstacles;
    private final Driver driver;
    private final int lanesHorizontal;
    private final int lanesVertical;
    private final int verticalLanesCapForColission;

    public int getspeedObstacle() {
        return speedObstacle;
    }

    public GameManager(int lifes, int lanesHorizontal, int lanesVertical,
                       int verticalLanesCapForColission) {
        this.initial_lifes = lifes;
        this.lanesHorizontal = lanesHorizontal;
        this.lanesVertical = lanesVertical;
        this.verticalLanesCapForColission = verticalLanesCapForColission;

        this.score = 0;
        this.deaths = 0;
        this.isStarted = false;
        this.driver = (Driver) new Driver().setHorLane((int) Math.floor(this.lanesHorizontal/2.0)).
                setVerLane(this.lanesVertical-1);
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
        int hlane = driver.getHorizontalLane();
        if (hlane + 1 == this.lanesHorizontal)
            return;
        hlane += 1;
        this.driver.setHorLane(hlane);
        for (ObstacleListener lis: this.obstacleListeners)
            lis.driverMoved(hlane);
    }
    public void moveDriverLeft()
    {
        int hlane = driver.getHorizontalLane();
        if (hlane == 0)
            return;
        hlane -= 1;
        this.driver.setHorLane(hlane);
        for (ObstacleListener lis: this.obstacleListeners)
            lis.driverMoved(hlane);
    }


    public boolean isGameEnded() {
        return initial_lifes == deaths;
    }

    public void checkColision() {
        Obstacle obsHit = null;
        for (Obstacle obs: this.obstacles ) {
            int obsHLane = obs.getHorizontalLane();
            int driverHLane = driver.getHorizontalLane();
            int obsVLane = obs.getVerticalLane();
            int driverVLane = driver.getVerticalLane();

            if(obsHLane != driverHLane) continue;

            if(Math.abs(obsVLane - driverVLane) < verticalLanesCapForColission)
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
            deaths++;
            for (ObstacleListener lis: this.obstacleListeners) {
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
        // moving obstacle might remove them by view, if leave screen
        // so using a city list, to not change original mid loop
        ArrayList<Obstacle> copylist = new ArrayList<>(this.obstacles);
        for (Obstacle obs: copylist) {
            obs.setVerLane(obs.getVerticalLane() + this.speedObstacle);
            obstacleListeners.forEach((el) -> el.ObstacleMoved(obs));
        }

        checkColision();
    }

    public void CreateBlock(long milisUntilFinished)
    {
        Random seed = new Random();
        int lane = seed.nextInt(this.lanesHorizontal);

        /*
        ArrayList<laneEnum> keyList = new ArrayList<>(lanes.keySet());
        int ind = seed.nextInt(keyList.size());
        laneEnum randomLane = keyList.get(ind);
        Position pos = lanes.get(randomLane);
        */

        Obstacle obs = (Obstacle) new Obstacle().setHorLane(lane).setVerLane(0);
        this.obstacles.add(obs);

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
    // #getters
    public int getScore() {
        return score;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getInitial_lifes() {
        return initial_lifes;
    }

    public GameManager setSpeedObstacle(int speed) {
        this.speedObstacle = speed;
        return this;
    }
    // #end getters
}
