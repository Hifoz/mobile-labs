package com.example.hifoz.lab3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

/**
 * The view containing the game
 */
@SuppressLint("ViewConstructor") // For this use case of the vieww, the extra constructors are unnecessary
public class BouncingBallView extends View {
    private RotationEventListener rotationEventListener;

    private Point vel = new Point(0,0);
    private int isCollided = 0;

    private int vWidth;
    private int vHeight;

    private int bgPadding;
    private Paint bgPaint;

    private static Ball ball;

    public BouncingBallView(Context context, RotationEventListener rel) {
        super(context);
        rotationEventListener = rel;

        bgPadding = 10;
        bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);

        Paint ballPaint = new Paint();
        ballPaint.setColor(Color.RED);
        ball = new Ball(50, 50, 5, ballPaint);

        startAnimation();
    }

    /*
     * ANIMATION SECTION
     *
     * Makes the canvas re-draw regularly
     */


    long mAnimStartTime;
    private boolean isPlaying = false;
    private Handler mHandler = new Handler();
    private Runnable mTick = new Runnable() {
        public void run() {
            invalidate();
            mHandler.postDelayed(this, 20); // 20ms == 60fps
        }
    };

    public void startAnimation() {
        if(isPlaying)
            return;
        mAnimStartTime = SystemClock.uptimeMillis();
        mHandler.removeCallbacks(mTick);
        mHandler.post(mTick);
        isPlaying = true;
    }

    public void stopAnimation() {
        mHandler.removeCallbacks(mTick);
        isPlaying = false;
    }

    @Override
    protected void onDraw(Canvas canvas){
        updateVelocity();
        updateBall();

        canvas.drawRect(bgPadding, bgPadding, vWidth - bgPadding, vHeight - bgPadding, bgPaint);
        canvas.drawCircle(ball.x, ball.y, ball.radius, ball.paint);
    }

    /*
     * GAME UPDATE SECTION
     *
     * Updates the ball
     */

    /**
     * Updates the ball's position and plays feedback if there is a collision
     */
    private void updateBall() {
        Point newPos = new Point(ball.x + vel.x, ball.y + vel.y);


        int collisionResult = testCollision(newPos);

        if(collisionResult != 0)
            newPos = applyCollision(new Point(ball.x, ball.y), vel);

        if(((collisionResult & 1) != 0 && (isCollided & 1) == 0) || ((collisionResult & 2) != 0 && (isCollided & 2) == 0))
            ((BouncingBallActivity)getContext()).playSound();

        isCollided = collisionResult;

        ball.x = newPos.x;
        ball.y = newPos.y;
    }

    /**
     * Tests if the ball will collide
     * @param pos position to test
     * @return if the ball is collidiong at given position
     */
    private int testCollision(Point pos){
        int res = 0;
        if (pos.x <= bgPadding + ball.radius || pos.x >= vWidth - bgPadding - ball.radius)
            res += 1;
        if(pos.y <= bgPadding + ball.radius || pos.y >= vHeight - bgPadding - ball.radius)
            res += 2;
        return res;
    }

    /**
     * Make the ball move as far as it can given its velocity, without colliding
     * @param pos the position of the ball
     * @param velocity the velocity of the ball
     * @return the new position
     */
    private Point applyCollision(Point pos, Point velocity){
        if(velocity.x > 0){
            pos.x = Math.min(pos.x + vel.x, vWidth - bgPadding - ball.radius);
        } else if( velocity.x < 0){
            pos.x = Math.max(pos.x + vel.x, bgPadding + ball.radius);
        }

        if(velocity.y > 0){
            pos.y = Math.min(pos.y + vel.y, vHeight - bgPadding - ball.radius);
        } else if( velocity.y < 0){
            pos.y = Math.max(pos.y + vel.y, bgPadding + ball.radius);
        }

        return pos;
    }

    /**
     * Updates the ball's velocity usign the device sensors
     */
    public void updateVelocity(){
        float[] rotation = rotationEventListener.getEulerRotation();
        int[] tilt = new int[2];
        tilt[0] = (int)Math.toDegrees(rotation[1]);
        tilt[1] = (int)Math.toDegrees(rotation[2]);

        int ignoredDegrees = 2;

        if(Math.abs(tilt[0]) > ignoredDegrees)
            vel.x = (int)(-tilt[0] * 0.25f);
        else vel.x = 0;

        if(Math.abs(tilt[1]) > ignoredDegrees)
            vel.y = (int)(-tilt[1] * 0.25f);
        else vel.y = 0;
    }

    /*
     * MISC
     */

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
