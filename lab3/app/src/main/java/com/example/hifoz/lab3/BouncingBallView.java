package com.example.hifoz.lab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

public class BouncingBallView extends View {

    int vWidth;
    int vHeight;

    int bgPadding;
    Paint bgPaint;

    public static Ball ball;

    public BouncingBallView(Context context) {
        super(context);

        bgPadding = 10;
        bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);

        Paint ballPaint = new Paint();
        ballPaint.setColor(Color.RED);
        ball = new Ball(50, 50, 5, ballPaint);

        startAnimation();


    }

    long mAnimStartTime;

    Handler mHandler = new Handler();
    Runnable mTick = new Runnable() {
        public void run() {
            invalidate();
            mHandler.postDelayed(this, 20); // 20ms == 60fps
        }
    };


    void startAnimation() {
        mAnimStartTime = SystemClock.uptimeMillis();
        mHandler.removeCallbacks(mTick);
        mHandler.post(mTick);
    }

    void stopAnimation() {
        mHandler.removeCallbacks(mTick);
    }

    @Override
    protected void onDraw(Canvas canvas){
        updateBall();

        canvas.drawRect(bgPadding, bgPadding, vWidth - bgPadding, vHeight - bgPadding, bgPaint);
        canvas.drawCircle(ball.x, ball.y, ball.radius, ball.paint);
    }

    int dirX = -1; // todo: temp, only to be used until we get sensors working
    int dirY = -1; // todo: temp, only to be used until we get sensors working
    private void updateBall() {
        Point velocity = new Point(dirX, dirY);

        Point newPos = new Point(ball.x + velocity.x, ball.y + velocity.y);

        int collisionResult = testCollision(newPos);
        if(collisionResult == 1) {
            dirX = -dirX;
            handleCollision();
        } else if (collisionResult == 2) {
            dirY = -dirY;
            handleCollision();
        } else {
            ball.x = newPos.x;
            ball.y = newPos.y;
        }

    }

    private int testCollision(Point pos){
        if (pos.x <= bgPadding + ball.radius || pos.x >= vWidth - bgPadding - ball.radius)
            return 1;
        else if(pos.y <= bgPadding + ball.radius || pos.y >= vHeight - bgPadding - ball.radius)
            return 2;
        return 0;
    }


    private void handleCollision(){

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH){
        super.onSizeChanged(w, h, oldW, oldH);

        vWidth = w;
        vHeight = h;

        ball.x = vWidth / 2;
        ball.y = vHeight / 2;
        ball.radius = vWidth / 20;

    }


}
