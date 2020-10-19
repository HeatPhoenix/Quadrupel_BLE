package com.example.quadrupel_ble;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
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
    private int sb_default = 128;

    Packet packet = new Packet();
    AckData currentACK;

    private TextView battery_text;
    private TextView roll_text;
    private TextView pitch_text;
    private TextView yaw_text;

    private TextView m1_text;
    private TextView m2_text;
    private TextView m3_text;
    private TextView m4_text;

    private TextView mode_text;
    private TextView flags_text;
    private boolean ping_flag = false;
    private int timeout_counter = 0;


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
        pitch_sb = findViewById(R.id.pitch_seekbar);
        pitch_sb.setMax(255);
        yaw_sb = findViewById(R.id.yaw_seekbar);
        yaw_sb.setMax(255);

        resetSeekbars(true);

        battery_text = findViewById(R.id.battery_text);
        roll_text = findViewById(R.id.roll_text);
        pitch_text = findViewById(R.id.pitch_text);
        yaw_text = findViewById(R.id.yaw_text);

        m1_text = findViewById(R.id.m1_text);
        m2_text = findViewById(R.id.m2_text);
        m3_text = findViewById(R.id.m3_text);
        m4_text = findViewById(R.id.m4_text);

        mode_text = findViewById(R.id.mode_text);
        flags_text = findViewById(R.id.flags_text);

        this.bluetoothManager = new DeviceBluetoothManager(getApplicationContext(), this);

        final Handler mHandler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (connected && ping_flag) {
                    //ctrl
                    if(roll_sb.getProgress() != 128 || pitch_sb.getProgress() != 128 || yaw_sb.getProgress() != 128 || throttle_sb.getProgress() != 0)
                    {
                        sendPacket(Commands.send_ctrl((byte)(throttle_sb.getProgress() & 0xFF), (byte) roll_sb.getProgress(), (byte) pitch_sb.getProgress(), (byte) throttle_sb.getProgress()));
                        if(!isTouching)
                            resetSeekbars(false);
                        Log.i("WDThandler", "control packet");
                    }
                    else//ping
                    {
                        sendPacket(Commands.send_ping());
                        Log.i("WDThandler", "pinged");
                    }
                    ping_flag = false;
                }
                else if(ping_flag == false)
                {
                    timeout_counter++;
                    if(timeout_counter > 0)
                    {
                        ping_flag = true;
                        timeout_counter = 0;
                    }
                }
                mHandler.postDelayed(this, 250);//twice per watchdog timer we ping
                roll_sb.setProgress(sb_default);
                pitch_sb.setProgress(sb_default);
                yaw_sb.setProgress(sb_default);
            }
        };
        mHandler.post(runnable);
    }

    boolean isTouching = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();

        int eventaction = event.getAction();

        switch (eventaction)
        {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                break;

            case MotionEvent.ACTION_UP:
                isTouching = false;
                break;
        }

        boolean ret = super.dispatchTouchEvent(event);
        return ret;
    }

    private void resetSeekbars(boolean resetThrottle)
    {
        if(resetThrottle)
            throttle_sb.setProgress(0);//0 to 255
        pitch_sb.setProgress(128);//-128 for drone data
        roll_sb.setProgress(128);//-128 for drone data
        yaw_sb.setProgress(128);//-128 for drone data
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
            connected = false;
            bluetoothManager.disconnectDevice();
            connection_button.setEnabled(true);
            updateUI(false);
            ping_button.setEnabled(false);
        }
        //System.exit(0);
    }



    @Override
    public void onDeviceLocated(boolean didSucceed) {
        Log.i("onDeviceLocated", "Outcome = " + didSucceed);
        if (didSucceed) {
            try {
                this.bluetoothManager.connectDevice(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            connected = false;
            ping_button.setEnabled(false);
            connection_button.setEnabled(true);
        }
        updateUI(connected);
    }

    @Override
    public void onBluetoothConnect(final boolean didSucceed) {
        // Log message
        Log.i("onBluetoothConnect", "Outcome = " + didSucceed);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connection_button.setEnabled(true);
                connected = true;

                updateUI(didSucceed);
                if(didSucceed == true)
                     ping_button.setEnabled(didSucceed);
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

        if(packet.type == Protocol.packet_type.ACK)
        {
            currentACK = new AckData(packet.data.raw);

            m1_text.setText(""+currentACK.ae0_approx);
            m2_text.setText(""+currentACK.ae1_approx);
            m3_text.setText(""+currentACK.ae2_approx);
            m4_text.setText(""+currentACK.ae3_approx);

            battery_text.setText(""+currentACK.battery_approx);
            roll_text.setText(""+currentACK.phi);
            pitch_text.setText(""+currentACK.theta);
            yaw_text.setText(""+currentACK.psi);

            mode_text.setText(""+currentACK.mode);
            flags_text.setText(""+currentACK.flags);
        }
    }

    @Override
    public void onCharacteristicChanged(byte[] value) {
        Log.i("onCharacteristicChanged", "Received " + value.length + " bytes of data!");



        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                data_textbox.setText("");
            }
        });

        for(byte b : value)
        {
            String toPrint = Integer.toHexString((int) b).toUpperCase();

            if(toPrint.length() > 2)
                toPrint = toPrint.substring(toPrint.length() - 2);

            final String finalToPrint = toPrint;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    data_textbox.append("0x"+ finalToPrint + " ");
                }
            };

            runOnUiThread(runnable);
            Log.i("protocolRetVal", "Returned: " + Integer.toString(Protocol.protocol_parse(b, packet)));
        }
        Log.i("protocol", "Packet CRC: " + packet.crc);

        if(packet.type == Protocol.packet_type.ACK || packet.type == Protocol.packet_type.NACK)
        {
            //update ui
            updateUI(connected);
            ping_flag = true;
        }
        else
        {
            //error?
        }
    }

    @Override
    public void onCharacteristicWrite(boolean didSucceed) {
        Log.i("onCharacteristicWrite", "Outcome of write = " + didSucceed);

    }

    public void pingButton(View view) {
        //bluetoothManager.enqueueMessageBuffer(new byte[]{(byte) 0xFF, (byte) 0x02, (byte) 0x00, (byte) 0x16});//FF 02 00 16 (ping)
        sendPacket(Commands.send_ping());
    }

    private void sendPacket(Packet pkt)
    {
        if(connected)
            bluetoothManager.enqueueMessageBuffer(pkt.toData());
    }

    public void bluetoothImageButton(View view) {
        if(connected)
        {
            Toast.makeText(getApplicationContext(), "Disabled Wireless!", Toast.LENGTH_LONG).show();

        }
    }

    public void onSafeButton(View view) {
        sendPacket(Commands.send_mode(Protocol.modes.MODE_SAFE.toByte()));
    }

    public void onPanicButton(View view) {
        sendPacket(Commands.send_panic());//not a mode, i guess
    }

    public void onCalibrateButton(View view) {
        sendPacket(Commands.send_mode(Protocol.modes.MODE_CALIB.toByte()));
    }

    public void onYawButton(View view) {
        sendPacket(Commands.send_mode(Protocol.modes.MODE_YAW.toByte()));
    }

    public void onFullButton(View view) {
        sendPacket(Commands.send_mode(Protocol.modes.MODE_FULL.toByte()));
    }

    public void onRawButton(View view) {
        sendPacket(Commands.send_mode(Protocol.modes.SET_RAW_SENSORS.toByte()));
    }

    public void onHeightButton(View view) {
        sendPacket(Commands.send_mode(Protocol.modes.SET_HEIGHT_CTL.toByte()));
    }

    public void onManualButton(View view) {
        sendPacket(Commands.send_mode(Protocol.modes.SET_WIRELESS.toByte()));
    }
}