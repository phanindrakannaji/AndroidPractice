package com.example.android.boundserviceexample;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

public class MyBoundedService extends Service implements SensorEventListener {

    MyBinder myBinder = new MyBinder();
    Sensor accelerometer;
    List<Double> sensorValues = new ArrayList<>();
    int index = 0;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        storeData(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    class MyBinder extends Binder {
        MyBoundedService getService(){
            return MyBoundedService.this;
        }
    }

    public MyBoundedService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public void storeData(SensorEvent event){
        synchronized (this) {
            float accX = event.values[0];
            float accY = event.values[1];
            float accZ = event.values[2];
            Double data = Math.sqrt(Math.pow(accX, 2) + Math.pow(accY, 2) + Math.pow(accZ, 2));
            if (index == 100) {
                index = 0;
            }
            sensorValues.add(index, data);
            index ++;
        }
    }

    public String getRMSAcc(){
        synchronized (this) {
            int count = 0;
            Double rmsValue = 0.0;
            for (Double data : sensorValues) {
                rmsValue = rmsValue + data;
                count++;
            }
            rmsValue = rmsValue / count;
            return rmsValue.toString();
        }
    }
}
