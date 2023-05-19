package com.example.ex1.Models;

public class GridObject {
    //private int imageResource;
    private int verticalLane;
    private int horizontalLane;



    public GridObject() {
    }

    public int getVerticalLane() {
        return verticalLane;
    }

    public int getHorizontalLane() {
        return horizontalLane;
    }

    public GridObject setHorLane(int horLane) {
        this.horizontalLane = horLane;
        return this;
    }


    public GridObject setVerLane(int verLane) {
        this.verticalLane = verLane;
        return this;
    }

    /*
    public int getImageResource() {
        return imageResource;
    }

    public GridObject ImageResource(int imageResource) {
        this.imageResource = imageResource;
        return this;
    }
     */
}
