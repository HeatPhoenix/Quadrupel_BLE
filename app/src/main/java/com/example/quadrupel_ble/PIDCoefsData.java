package com.example.quadrupel_ble;

import java.nio.ByteBuffer;

public class PIDCoefsData{
    byte[] P = new byte[2];
    byte[] I = new byte[2];
    byte[] D = new byte[2];
    byte axis;


    public byte[] toArray()
    {
        byte[] data = new byte[7];

        return data;
    }

    public PIDCoefsData(double P_float, double I_float, double D_float, byte axis_val)
    {
        P = ByteBuffer.allocate(4).putInt(Fix.toFix(P_float)).array();
        I = ByteBuffer.allocate(4).putInt(Fix.toFix(I_float)).array();
        D = ByteBuffer.allocate(4).putInt(Fix.toFix(D_float)).array();
        axis = axis_val;
    }
}
