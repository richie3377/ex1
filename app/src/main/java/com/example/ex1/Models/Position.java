package com.example.ex1.Models;

public class Position {
    private double topleftx;
    private double toplefty;

    public Position() {
    }

    public Position(double topleftx, double toplefty) {
        this.topleftx = topleftx;
        this.toplefty = toplefty;
    }

    public double getTopleftx() {
        return topleftx;
    }

    public void setTopleftx(double topleftx) {
        this.topleftx = topleftx;
    }

    public double getToplefty() {
        return toplefty;
    }

    public void setToplefty(double toplefty) {
        this.toplefty = toplefty;
    }
}
