package com.example.hifoz.lab3;

import android.graphics.Paint;


public class Ball{
    public float x;
    public float y;
    public float radius;
    public Paint paint;

    public Ball(float x, float y, float radius, Paint paint){
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.paint = paint;
    }
}
