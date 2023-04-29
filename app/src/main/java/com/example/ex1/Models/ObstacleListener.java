package com.example.ex1.Models;

public interface ObstacleListener {
    void ObstacleMoved(Obstacle obs);
    void ObstacleCreated(Obstacle obs);
    void ObstacleHit(Obstacle obs);
    void ObstacleRemoved(Obstacle obs);
    void driverMoved(Position pos);
}
