package com.example.hifoz.lab3;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class BouncingBallActivity extends AppCompatActivity {
    private BouncingBallView view;
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private RotationEventListener rotationEventListener;
    private MediaPlayer soundPlayer;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        if(sensorManager == null){
            Toast.makeText(this, "Not able to access sensor service", Toast.LENGTH_LONG).show();
            finish();
        }
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        rotationEventListener = new RotationEventListener();
        rotationEventListener.context = this;

        soundPlayer = MediaPlayer.create(this, R.raw.collision_sound);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        view = new BouncingBallView(this, rotationEventListener);
        setContentView(view);
    }


    @Override
    protected void onResume() {
        sensorManager.registerListener(rotationEventListener, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        view.startAnimation();
        super.onResume();
    }

    @Override
    protected void onPause() {
        view.stopAnimation();
        sensorManager.unregisterListener(rotationEventListener, rotationSensor);
        super.onPause();
    }

    /**
     * Plays a sound and a vibration effect
     */
    public void playSound(){
        soundPlayer.start();
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(200);
            }
        }
    }
}
