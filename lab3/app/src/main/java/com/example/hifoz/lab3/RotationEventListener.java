package com.example.hifoz.lab3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.renderscript.Matrix4f;

/**
 * Used to listen for changes on the rotation of the device
 */
public class RotationEventListener implements SensorEventListener {
    public Context context;
    private Matrix4f rotMatrix = new Matrix4f();

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR)
            return;

        SensorManager.getRotationMatrixFromVector(rotMatrix.getArray(), event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * Gets the euler rotation of the phone
     * @return a float[3] with the euler rotation
     */
    public float[] getEulerRotation(){
        float[] rad = new float[3];
        SensorManager.getOrientation(rotMatrix.getArray(), rad);
        return rad;
    }

}
