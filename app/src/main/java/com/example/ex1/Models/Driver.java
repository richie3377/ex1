package com.example.ex1.Models;

public class Driver {
    private int imageResource;

    private Position position;
    private laneEnum lane;



    public Driver() {
    }

    public laneEnum getLane() {
        return lane;
    }

    public Driver setLane(laneEnum lane) {
        this.lane = lane;
        return this;
    }

    public Position getPosition() {
        return position;
    }

    public Driver setPosition(Position position) {
        this.position = position;
        return this;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void move(double offsetx, double offsety)
    {
        position.setTopleftx(position.getTopleftx()+offsetx);
        position.setToplefty(position.getToplefty()+offsety);
    }

    public Driver setImageResource(int imageResource) {
        this.imageResource = imageResource;
        return this;
    }
}
