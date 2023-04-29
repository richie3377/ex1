package com.example.ex1.Models;

public class Obstacle {
    private int imageResource;

    public Position getPosition() {
        return position;
    }

    public Obstacle setPosition(Position position) {
        this.position = position;
        return this;
    }

    private Position position;


    private laneEnum lane;

    public Obstacle() {
    }

    public laneEnum getLane() {
        return lane;
    }

    public Obstacle setLane(laneEnum lane) {
        this.lane = lane;
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

    public Obstacle setImageResource(int imageResource) {
        this.imageResource = imageResource;
        return this;
    }

    /*


Built-in hashCode() and equals() works fine. They're consistent in the way required by HashMap
(i.e.: if hashCode()s aren't identical, equals() will always return false).
That's because every object instance will only be equal() to itself.

However this is often not what you want it to do. Specifically, without overriding these methods,
two instances of Obstable with identical fields will not be considered equal
     */
}
