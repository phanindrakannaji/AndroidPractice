package com.example.android.boundedservicepractice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    TextView accX;
    Button btClickMe;
    boolean bound = false;
    MyBoundedService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accX = (TextView) findViewById(R.id.accX);
        btClickMe = (Button) findViewById(R.id.bt_clickme);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btClickMe.setOnClickListener(this);
        Intent myIntent = new Intent(this, MyBoundedService.class);
        bindService(myIntent, myServiceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        btClickMe.setOnClickListener(null);
        unbindService(myServiceConnection);
    }

    ServiceConnection myServiceConnection = new ServiceConnection() {
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
    public void onClick(View view) {
        if(bound){
            Float avgValue = myService.getAvgValue();
            accX.setText(String.valueOf(avgValue));
        } else{
            accX.setText("Not yet connected!");
        }
    }
}
