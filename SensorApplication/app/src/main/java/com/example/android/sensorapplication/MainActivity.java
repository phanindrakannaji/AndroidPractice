package com.example.android.sensorapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    EditText accX, accY, accZ;
    Handler myHandler = new Handler();
    SensorManager sensorManager;
    Sensor accelerometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accX = (EditText) findViewById(R.id.accX);
        accY = (EditText) findViewById(R.id.accY);
        accZ = (EditText) findViewById(R.id.accZ);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        AccelerometerTask myTask = new AccelerometerTask(sensorEvent);
        myHandler.post(myTask);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    class AccelerometerTask implements Runnable{

        SensorEvent sensorEvent;

        AccelerometerTask(SensorEvent event){
            sensorEvent = event;
        }
        @Override
        public void run() {
            accX.setText(String.valueOf(sensorEvent.values[0]));
            accY.setText(String.valueOf(sensorEvent.values[1]));
            accZ.setText(String.valueOf(sensorEvent.values[2]));
        }
    }
}
