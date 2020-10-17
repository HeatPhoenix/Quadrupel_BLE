package com.example.quadrupel_ble;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DeviceBluetoothInterface{

    private DeviceBluetoothManager bluetoothManager;
    private Boolean connected = false;
    private TextView data_textbox;
    private ImageView connection_icon;
    private Button connection_button;
    private Button ping_button;

    private SeekBar throttle_sb;
    private SeekBar roll_sb;
    private SeekBar yaw_sb;
    private SeekBar pitch_sb;

    Packet packet = new Packet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Protocol.protocol_init();
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        connection_button = findViewById(R.id.connection_button);

        ping_button = findViewById(R.id.ping_button);
        ping_button.setEnabled(false);

        connection_icon = findViewById(R.id.connection_icon);
        connection_icon.setColorFilter(Color.parseColor("#FF0000"));

        data_textbox = findViewById(R.id.ReceivedData);

        throttle_sb = findViewById(R.id.throttle_seekbar);
        throttle_sb.setMax(255);
        roll_sb = findViewById(R.id.roll_seekbar);
        roll_sb.setMax(255);
        roll_sb.setProgress(128);//-128 for drone data
        pitch_sb = findViewById(R.id.pitch_seekbar);
        pitch_sb.setMax(255);
        pitch_sb.setProgress(128);//-128 for drone data
        yaw_sb = findViewById(R.id.yaw_seekbar);
        yaw_sb.setMax(255);
        yaw_sb.setProgress(128);//-128 for drone data



        this.bluetoothManager = new DeviceBluetoothManager(getApplicationContext(), this);
    }

    public void connectionButton(View view) {
        Toast.makeText(getApplicationContext(), "Attempting Connection. . .", Toast.LENGTH_LONG).show();
        connection_button.setEnabled(false);
        if (this.connected == false) {
            try {
                bluetoothManager.scanForDevice("QUAD RX-79");
            } catch (DeviceBluetoothException ex) {
                ex.printStackTrace();
            }
        } else {
            bluetoothManager.disconnectDevice();
            connected = false;
            connection_button.setEnabled(true);
            updateUI(false);
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
            connected = false;
            connection_button.setEnabled(true);
        }
        updateUI(connected);
    }

    @Override
    public void onBluetoothConnect(boolean didSucceed) {
        // Log message
        Log.i("onBluetoothConnect", "Outcome = " + didSucceed);

        updateUI(didSucceed);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connection_button.setEnabled(true);
            }
        });

        // Update the user interface
        //this.button_toggle_bluetooth.setIsActive((this.connected = didSucceed));
    }

    public void updateUI(boolean connected)
    {
        if(connected)
        {
            connection_icon.setColorFilter(Color.parseColor("#00FF00"));
            connection_button.setText("Disconnect");
        }else
        {
            connection_icon.setColorFilter(Color.parseColor("#FF0000"));
            connection_button.setText("Connect");
        }
        ping_button.setEnabled(connected);
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

    public void onMode0Button(View view) {
    }

    public void onMode1Button(View view) {
    }

    public void onMode2Button(View view) {
    }

    public void onMode3Button(View view) {
    }

    public void onMode4Button(View view) {
    }

    public void onMode5Button(View view) {
    }

    public void onMode6Button(View view) {
    }

    public void onMode7Button(View view) {
    }



    public void bluetoothImageButton(View view) {
        if(connected)
        {
            Toast.makeText(getApplicationContext(), "Disabled Wireless!", Toast.LENGTH_LONG).show();

        }
    }
}