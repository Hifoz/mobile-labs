package com.example.hifoz.lab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.View;

public class BouncingBallView extends View {
    int vWidth;
    int vHeight;

    int bgPadding;
    Rect bgRect;
    Paint bgPaint;

    Ball ball;


    public BouncingBallView(Context context) {
        super(context);

        bgPadding = 10;
        bgRect = new Rect();
        bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);

        Paint ballPaint = new Paint();
        ballPaint.setColor(Color.RED);
        ball = new Ball(0.5f, 0.5f, 0.05f, ballPaint);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH){
        super.onSizeChanged(w, h, oldW, oldH);

        vWidth = w;
        vHeight = h;

        bgRect.set(bgPadding, bgPadding, vWidth - bgPadding, vHeight - bgPadding);
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawRect(bgRect, bgPaint);
        canvas.drawCircle(ball.x * vWidth, ball.y * vHeight , ball.radius * vWidth, ball.paint);
    }




    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        vHeight = MeasureSpec.getSize(heightMeasureSpec);
        vWidth = MeasureSpec.getSize(widthMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private static class BouncingBallTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return null;
        }
    }
}
