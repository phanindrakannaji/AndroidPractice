package com.example.android.bluetoothexample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ENABLE_BLUETOOTH_INTENT = 100;
    private BluetoothAdapter bluetoothAdapter;
    HashMap<String, String> devices;
    ListView devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devicesList = (ListView) findViewById(R.id.deviceList);
        devices = new HashMap<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();
        if (!bluetoothAdapter.isDiscovering()){
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivity(discoverableIntent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "No bluetooth adapter", Toast.LENGTH_SHORT).show();
        } else{
            if (!bluetoothAdapter.isEnabled()){
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, ENABLE_BLUETOOTH_INTENT);
            } else{
                doWork();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void doWork() {
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        bluetoothAdapter.startDiscovery();
        registerReceiver(broadcastReceiver, intentFilter);
    }


    private List<String> values;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!devices.containsKey(bluetoothDevice.getAddress())) {
                    devices.put(bluetoothDevice.getAddress(), bluetoothDevice.getName() + " - " + bluetoothDevice.getAddress());
                    values = new ArrayList<>();
                    for (String value : devices.values()) {
                        values.add(value);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,
                            android.R.id.text1, values);
                    devicesList.setAdapter(adapter);
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_BLUETOOTH_INTENT){
            if (resultCode == RESULT_OK){
                Toast.makeText(getApplicationContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                doWork();
            }
        }
    }
}