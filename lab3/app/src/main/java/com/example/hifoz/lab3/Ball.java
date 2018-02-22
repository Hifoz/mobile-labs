package com.example.hifoz.lab3;

import android.graphics.Paint;

/**
 * Data structure containg data on a ball
 */
public class Ball{
    public int x;
    public int y;
    public int radius;
    public Paint paint;

    public Ball(int x, int y, int radius, Paint paint){
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.paint = paint;
    }
}
