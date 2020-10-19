package com.example.quadrupel_ble;

public class Commands {
    public static Packet send_ping() {
        Packet ping_packet = new Packet(Protocol.packet_type.PING, (byte) 0, new PacketData());
        return ping_packet;
    }

    public static Packet send_panic() {
        Packet panic_packet = new Packet(Protocol.packet_type.STOP, (byte) 0, new PacketData());
        return panic_packet;
    }

    public static Packet send_mode(byte mode)
    {
        Packet mode_packet = new Packet(Protocol.packet_type.MODE_SWITCH, (byte) 1, new PacketData(mode));
        return mode_packet;
    }

    public static Packet send_ctrl(byte lift, byte yaw, byte pitch, byte roll)
    {
        Packet ctrl_packet = new Packet(Protocol.packet_type.CTRL, (byte) 4, new PacketData(new byte[]{lift, yaw, pitch, roll}));
        return ctrl_packet;
    }

    //PIDCOEFS sizeof = 2*3 + 1... I think
    public static Packet adjust_yaw_P(byte[] P){

        Packet p_packet = new Packet(Protocol.packet_type.PIDCOEFS, (byte) 7, new PacketData(P));
        return p_packet;
    }

    //ignoring Ps for now


}

