package com.example.android.serviceapplication;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etRms;
    Button btRms;
    boolean bound = false;
    MyBoundedService myService;
    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etRms = (EditText) findViewById(R.id.et_rms);
        btRms = (Button) findViewById(R.id.bt_getrms);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btRms.setOnClickListener(this);
        Intent myIntent = new Intent(this, MyBoundedService.class);
        bindService(myIntent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBoundedService.MyBinder myBinder = (MyBoundedService.MyBinder) iBinder;
            myService = myBinder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        btRms.setOnClickListener(null);
        unbindService(serviceConnection);
    }

    @Override
    public void onClick(View view) {
        GravityTask gTask = new GravityTask();
        myHandler.post(gTask);
    }

    class GravityTask implements Runnable{

        @Override
        public void run() {
            if (bound){
                float rmsGravity = myService.getRmsGravity();
                etRms.setText(String.valueOf(rmsGravity));
            }
        }
    }
}
