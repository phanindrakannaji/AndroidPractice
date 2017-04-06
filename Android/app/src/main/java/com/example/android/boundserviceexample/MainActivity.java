package com.example.android.boundserviceexample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvRmsAcc;
    Button btClickMe;
    MyBoundedService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvRmsAcc = (TextView) findViewById(R.id.tv_rms_acc);
        btClickMe = (Button) findViewById(R.id.bt_getacc);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btClickMe.setOnClickListener(this);
        Intent myIntent = new Intent(this, MyBoundedService.class);
        bindService(myIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        btClickMe.setOnClickListener(null);
        unbindService(serviceConnection);
    }

    private boolean bounded;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBoundedService.MyBinder myBinder = (MyBoundedService.MyBinder) iBinder;
            myService = myBinder.getService();
            bounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bounded = false;
        }
    };

    @Override
    public void onClick(View view) {
        if (bounded) {
            String acc = myService.getRMSAcc();
            tvRmsAcc.setText(acc);
        }
    }
}
