package com.example.hifoz.lab3;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class BouncingBallActivity extends AppCompatActivity {
    private BouncingBallView view;
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private RotationEventListener rotationEventListener;

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

        view = new BouncingBallView(this, rotationEventListener);
        setContentView(view);
    }


    @Override
    protected void onResume() {
        sensorManager.registerListener(rotationEventListener, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(rotationEventListener, rotationSensor);
        super.onPause();
    }
}
