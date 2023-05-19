package com.example.ex1.Models;

public class Obstacle extends GridObject {

    //private int speed;

    public Obstacle() {
        super();
    }

    /*
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
*/
    /*


Built-in hashCode() and equals() works fine. They're consistent in the way required by HashMap
(i.e.: if hashCode()s aren't identical, equals() will always return false).
That's because every object instance will only be equal() to itself.

However this is often not what you want it to do. Specifically, without overriding these methods,
two instances of Obstable with identical fields will not be considered equal
     */
}
