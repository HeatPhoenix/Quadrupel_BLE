package com.example.quadrupel_ble;

public class AckData{
    short timestamp;        //0-1
    byte mode_flags;        //2
    int battery_approx;    //3
    byte errcode;           //4
    int ae0_approx;        //5
    int ae1_approx;
    int ae2_approx;
    int ae3_approx;        //8
    short phi;//-> roll     //9-10
    short theta;//-> pitch  //11-12
    short psi;//-> yaw      //13-14

    short mode;// from mode_flags
    short flags;//from flags

    public AckData(byte[] raw)
    {
        timestamp = (short) ((raw[0]<<8) | raw[1]);
        mode_flags = raw[2];
        battery_approx = ((int)(raw[3]/100)+10);
        errcode = raw[4];
        ae0_approx = Byte.toUnsignedInt(raw[5]);
        ae1_approx = Byte.toUnsignedInt(raw[6]);
        ae2_approx = Byte.toUnsignedInt(raw[7]);
        ae3_approx = Byte.toUnsignedInt(raw[8]);
        phi = (short) ((raw[9]<<8) | raw[10]);
        theta = (short) ((raw[11]<<8) | raw[12]);
        psi = (short) ((raw[13]<<8) | raw[14]);

        mode = (short) (mode_flags & 0x0F);
        flags = (short) (mode_flags/16);
    }
}
