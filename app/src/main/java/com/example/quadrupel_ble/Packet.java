package com.example.quadrupel_ble;

public class Packet {
    Protocol.packet_type type;
    byte len;
    PacketData data = new PacketData();
    byte crc;

    public Packet() {
        type = Protocol.packet_type.valueOf(0);
        len = 0;
        data = new PacketData();
        crc = 0;
    }

    public Packet(Protocol.packet_type p_type, byte p_len, PacketData p_data) {
        type = p_type;
        len = p_len;
        data = p_data;
        crc = 0;
    }

    public byte[] toData() {
        byte[] data_buffer = new byte[4 + len];
        data_buffer[0] = (byte) 0xFF;
        data_buffer[1] = type.toByte();
        data_buffer[2] = len;
        if (len != 0
        ) {
            for (int d_i = 0; d_i < len; d_i++) {
                data_buffer[3 + d_i] = this.data.raw[d_i];
            }
        }

        byte crc_val = 0;

        for(int i = 1; i < len + 3; i++)
        {
            crc_val = CRC.crc_update(crc_val, data_buffer[i]);
        }

        data_buffer[len + 3] = crc_val;

        return data_buffer;
    }
}
