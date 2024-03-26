package org.example;



public class Point {

    private double y;
    private double yDir;

    public Point(double y, double yDir) {
        this.y = y;
        this.yDir = yDir;
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getyDir() {
        return yDir;
    }

    public void setyDir(int yDir) {
        this.yDir = yDir;
    }
}