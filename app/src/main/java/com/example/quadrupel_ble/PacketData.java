package com.example.quadrupel_ble;

public class PacketData{
    byte[] raw = new byte[Protocol.MAX_PKT_LEN];

    public PacketData(byte data)
    {
        raw[0] = data;
    }

    public PacketData(byte[] data)
    {
        raw = data;
    }

    public PacketData()
    {

    }
}


