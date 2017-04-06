package com.example.android.serviceapplication;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

public class MyBoundedService extends Service implements SensorEventListener{

    MyBinder myBinder = new MyBinder();
    SensorManager sensorManager;
    Sensor accelerometer;
    Float gX = Float.valueOf(0);
    Float gY = Float.valueOf(0);
    Float gZ = Float.valueOf(0);
    float alpha = (float) 0.9;

    public class MyBinder extends Binder {
        public MyBoundedService getService(){
            return MyBoundedService.this;
        }
    }

    public MyBoundedService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this, accelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        synchronized (this){
            gX = alpha*gX + (1 - alpha)*sensorEvent.values[0];
            gY = alpha*gY + (1 - alpha)*sensorEvent.values[1];
            gZ = alpha*gZ + (1 - alpha)*sensorEvent.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public float getRmsGravity(){
        synchronized (this) {
            return (float) Math.sqrt(Math.pow(gX, 2) + Math.pow(gY, 2) + Math.pow(gZ, 2));
        }
    }
}
