package com.example.quadrupel_ble;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DeviceBluetoothInterface{

    private DeviceBluetoothManager bluetoothManager;
    private Boolean connected = false;
    private TextView data_textbox;
    private ImageView connection_icon;
    Packet packet = new Packet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Protocol.protocol_init();

        setContentView(R.layout.activity_main);
        connection_icon = findViewById(R.id.connection_icon);
        connection_icon.setColorFilter(Color.parseColor("#FF0000"));
        data_textbox = findViewById(R.id.ReceivedData);

        this.bluetoothManager = new DeviceBluetoothManager(getApplicationContext(), this);
    }

    public void connectionButton(View view) {
        Toast.makeText(getApplicationContext(), "Attempting Connection. . .", Toast.LENGTH_LONG).show();

        if (this.connected == false) {
            try {
                bluetoothManager.scanForDevice("QUAD RX-79");
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
    }

    @Override
    public void onCharacteristicChanged(byte[] value) {
        Log.i("onCharacteristicChanged", "Received " + value.length + " bytes of data!");
        for(byte b : value)
        {
            String toPrint = Integer.toHexString((int) b).toUpperCase();
            if(toPrint.length() > 2)
                toPrint = toPrint.substring(toPrint.length() - 2);
            data_textbox.append("0x"+toPrint + " ");
            Log.i("protocolRetVal", "Returned: " + Integer.toString(Protocol.protocol_parse(b, packet)));
        }
        Log.i("protocol", "Packet CRC: " + packet.crc);
    }

    @Override
    public void onCharacteristicWrite(boolean didSucceed) {
        Log.i("onCharacteristicWrite", "Outcome of write = " + didSucceed);

    }

    public void pingButton(View view) {
        bluetoothManager.enqueueMessageBuffer(new byte[]{(byte) 0xFF, (byte) 0x02, (byte) 0x00, (byte) 0x16});//FF 02 00 16 (ping)
    }
}