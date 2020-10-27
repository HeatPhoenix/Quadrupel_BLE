package com.example.quadrupel_ble;

import com.example.quadrupel_ble.Protocol.*;

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
    public static Packet adjust_yaw_P(Byte[] P){//length 7
        Protocol.DEFAULT_P_YAW = Protocol.addByteArrays(P, Protocol.DEFAULT_P_YAW);
        Packet p_packet = new Packet(Protocol.packet_type.PIDCOEFS, (byte) 7, new PacketData(new byte[]{Protocol.DEFAULT_P_YAW[1], Protocol.DEFAULT_P_YAW[0], 0, 0, 0, 0, ctl_axis.AXIS_YAW.toByte()}));
        return p_packet;
    }

    //PIDCOEFS sizeof = 2*3 + 1... I think
    public static Packet adjust_rollpitch_P1(Byte[] P){//length 7
        Protocol.DEFAULT_P1 = Protocol.addByteArrays(P, Protocol.DEFAULT_P1);
        Packet p_packet = new Packet(Protocol.packet_type.PIDCOEFS, (byte) 7, new PacketData(new byte[]{Protocol.DEFAULT_P1[1], Protocol.DEFAULT_P1[0], 0, 0, 0, 0, ctl_axis.AXIS_P1_ROLLPITCH.toByte()}));
        return p_packet;
    }

    //PIDCOEFS sizeof = 2*3 + 1... I think
    public static Packet adjust_rollpitch_P2(Byte[] P){//length 7
        Protocol.DEFAULT_P2 = Protocol.addByteArrays(P, Protocol.DEFAULT_P2);
        Packet p_packet = new Packet(Protocol.packet_type.PIDCOEFS, (byte) 7, new PacketData(new byte[]{Protocol.DEFAULT_P2[1], Protocol.DEFAULT_P2[0], 0, 0, 0, 0, ctl_axis.AXIS_P2_ROLLPITCH.toByte()}));
        return p_packet;
    }

    //PIDCOEFS sizeof = 2*3 + 1... I think
    public static Packet adjust_height_pi(Byte[] P, Byte[] I){//length 7
        Protocol.DEFAULT_P_HEIGHT = Protocol.addByteArrays(P, Protocol.DEFAULT_P_HEIGHT);
        Protocol.DEFAULT_I_HEIGHT = Protocol.addByteArrays(I, Protocol.DEFAULT_I_HEIGHT);
        Packet p_packet = new Packet(Protocol.packet_type.PIDCOEFS, (byte) 7, new PacketData(new byte[]{Protocol.DEFAULT_P_HEIGHT[1], Protocol.DEFAULT_P_HEIGHT[0], Protocol.DEFAULT_I_HEIGHT[1], Protocol.DEFAULT_I_HEIGHT[0], 0, 0, ctl_axis.AXIS_HEIGHT.toByte()}));
        return p_packet;
    }

}

