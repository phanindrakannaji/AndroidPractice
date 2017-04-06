package com.example.android.boundedservicepractice;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

public class MyBoundedService extends Service implements SensorEventListener{

    MyBinder myBinder = new MyBinder();
    List<Float> myValues = new ArrayList<>();
    int index = 0;
    Sensor accelerator;
    Handler myHandler = new Handler();

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        storeData(sensorEvent);
    }

    class MyAccelerometerTask implements Runnable{

        SensorEvent event;

        MyAccelerometerTask(SensorEvent event){
            this.event = event;
        }

        @Override
        public void run() {
            synchronized (this) {
                float rms = (float) Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
                if (index == 1000) {
                    index = 0;
                }
                myValues.add(index, rms);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    class MyBinder extends Binder{
        public MyBoundedService getService(){
            return MyBoundedService.this;
        }
    }

    public MyBoundedService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SensorManager mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerator = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, accelerator, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public void storeData(SensorEvent event){
        MyAccelerometerTask myTask = new MyAccelerometerTask(event);
        myHandler.post(myTask);
    }

    public float getAvgValue(){
        float avgValue = 0;
        synchronized (this){
            int count = 0;
            for(Float value : myValues){
                avgValue = avgValue + value;
                count++;
            }
            avgValue = avgValue/count;
        }
        myValues = new ArrayList<>();
        return avgValue;
    }
}
