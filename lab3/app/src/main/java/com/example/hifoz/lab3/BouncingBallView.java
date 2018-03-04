package com.example.hifoz.lab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

public class BouncingBallView extends View {
    RotationEventListener rotEventListener;

    float dirX = -1; // todo: temp, only to be used until we get sensors working
    float dirY = -1; // todo: temp, only to be used until we get sensors working

    int vWidth;
    int vHeight;

    int bgPadding;
    Paint bgPaint;

    public static Ball ball;

    public BouncingBallView(Context context, RotationEventListener rel) {
        super(context);
        rotEventListener = rel;

        bgPadding = 10;
        bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);

        Paint ballPaint = new Paint();
        ballPaint.setColor(Color.RED);
        ball = new Ball(50, 50, 5, ballPaint);

        startAnimation();
    }

    // ANIMATION:

    long mAnimStartTime;
    private Handler mHandler = new Handler();
    private Runnable mTick = new Runnable() {
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

    // DRAW:

    @Override
    protected void onDraw(Canvas canvas){
        updateTilt();
        updateBall();

        canvas.drawRect(bgPadding, bgPadding, vWidth - bgPadding, vHeight - bgPadding, bgPaint);
        canvas.drawCircle(ball.x, ball.y, ball.radius, ball.paint);
    }

    // PHYSICS:

    private void updateBall() {
        Point velocity = new Point((int)dirX, (int)dirY);

        Point newPos = new Point(ball.x + velocity.x, ball.y + velocity.y);

        int collisionResult = testCollision(newPos);
        if((collisionResult & 1) != 0) {
            newPos.x = ball.x;
            handleCollision();
        }
        if ((collisionResult & 2) != 0) {
            newPos.y = ball.y;
            handleCollision();
        }

        ball.x = newPos.x;
        ball.y = newPos.y;

    }

    private int testCollision(Point pos){
        int res = 0;
        if (pos.x <= bgPadding + ball.radius || pos.x >= vWidth - bgPadding - ball.radius)
            res += 1;
        else if(pos.y <= bgPadding + ball.radius || pos.y >= vHeight - bgPadding - ball.radius)
            res += 2;
        return res;
    }

    private void handleCollision(){
        Toast.makeText(getContext(), "Pling~~", Toast.LENGTH_SHORT).show();
    }

    // MISC:

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH){
        super.onSizeChanged(w, h, oldW, oldH);

        vWidth = w;
        vHeight = h;

        ball.x = vWidth / 2;
        ball.y = vHeight / 2;
        ball.radius = vWidth / 20;

    }

    public void updateTilt(){
        float[] rotation = rotEventListener.getTilt();
        int[] tilt = new int[2];
        tilt[0] = (int)Math.toDegrees(rotation[1]);
        tilt[1] = (int)Math.toDegrees(rotation[2]);

        if(tilt[0] < -1)
            dirX = 1;
        else if(tilt[0] > 1)
            dirX = -1;

        if(tilt[1] < -1)
            dirY = 1;
        else if(tilt[1] > 1)
            dirY = -1;




        //Toast.makeText(getContext(), rotation[0] + ", " + rotation[1] + ", " + rotation[2], Toast.LENGTH_SHORT).show();
    }
}
