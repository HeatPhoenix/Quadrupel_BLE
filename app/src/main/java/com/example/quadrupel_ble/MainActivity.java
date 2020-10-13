package com.example.quadrupel_ble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DeviceBluetoothInterface{

    private DeviceBluetoothManager bluetoothManager;
    private Boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bluetoothManager = new DeviceBluetoothManager(getApplicationContext(), this);
    }

    public void connectionButton(View view) {
        Toast.makeText(getApplicationContext(), "Attempting Connection. . .", Toast.LENGTH_LONG).show();

        if (this.connected == false) {
            try {
                bluetoothManager.scanForDevice("Quadrupel 46");
            } catch (DeviceBluetoothException ex) {
                ex.printStackTrace();
            }
        } else {
            bluetoothManager.disconnectDevice();
            connected = false;
        }
        //System.exit(0);
    }



    @Override
    public void onDeviceLocated(boolean didSucceed) {
        Log.i("onDeviceLocated", "Outcome = " + didSucceed);
        if (didSucceed) {
            try {
                this.bluetoothManager.connectDevice(getApplicationContext());
                connected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            refresh(true);
        }
    }

    @Override
    public void onBluetoothConnect(boolean didSucceed) {
        // Log message
        Log.i("onBluetoothConnect", "Outcome = " + didSucceed);

        // Reset everything in preparation for brush mode
        if (didSucceed) {
            //Toast.makeText(getApplicationContext(), "Connected.", Toast.LENGTH_LONG).show();
        }

        // Update the user interface
        //this.button_toggle_bluetooth.setIsActive((this.connected = didSucceed));
        refresh(true);
    }

    @Override
    public void onCharacteristicChanged(byte[] value) {
        Log.i("onCharacteristicChanged", "Received " + value.length + " bytes of data!");


        refresh(false);
    }

    @Override
    public void onCharacteristicWrite(boolean didSucceed) {
        Log.i("onCharacteristicWrite", "Outcome of write = " + didSucceed);
        Toast.makeText(getApplicationContext(), "Wrote data.", Toast.LENGTH_LONG).show();

    }

    public void refresh(boolean dismissLoadingDialog)
    {

    }

    public void pingButton(View view) {
        bluetoothManager.enqueueMessageBuffer(new byte[]{(byte) 0xFF, (byte) 0x02, (byte) 0x00, (byte) 0x16});//FF 02 00 16 (ping)
    }
}