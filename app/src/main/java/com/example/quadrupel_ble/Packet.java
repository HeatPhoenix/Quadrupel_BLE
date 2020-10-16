package com.example.quadrupel_ble;

public class Packet {
    Protocol.packet_type type;
    byte len;
    PacketData data;
    byte crc;

    public Packet()
    {
        type = Protocol.packet_type.valueOf(0);
        len = 0;
        data = new PacketData();
        crc = 0;
    }
}
