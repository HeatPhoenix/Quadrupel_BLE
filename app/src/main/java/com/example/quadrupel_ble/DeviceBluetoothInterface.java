package com.example.quadrupel_ble;

public interface DeviceBluetoothInterface {

    // Handles discovery event
    public void onDeviceLocated(boolean didSucceed);

    // Handle connection events
    public void onBluetoothConnect(boolean didSucceed);

    // Handle changes to the characteristic value
    public void onCharacteristicChanged(byte[] value);

    // Handle the result of a write operation to the characteristic
    public void onCharacteristicWrite(boolean didSucceed);

}
