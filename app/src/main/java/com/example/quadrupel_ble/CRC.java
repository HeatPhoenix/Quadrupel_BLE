package com.example.quadrupel_ble;

public class CRC {

    static byte[] _crc_table = new byte[256];   /* CRC remainder look-up table */

//    #define CRC_POLY (0x1d5 & 0xFF) /* The CRC polynomial, without the x^8 term */
//    #define WIDTH 8
//    #define TOPBIT(x) (x & (1 << (WIDTH - 1)))
    public static int WIDTH = 8;


    static byte TOPBIT(byte x)
    {
        return (byte) (x & (1 << (WIDTH - 1)));
    }


    public static void crc_init()
    {
        int i, j;
        byte remainder;

        /* For each possible input */
        for (i=0; i<0x100; i++) {
            remainder = (byte) i;

            /* Compute the remainder of the finite field division by CRC_POLY */
            for (j=0; j<8; j++) {
                if (TOPBIT(remainder) != 0) {
                    remainder = (byte) ((remainder << 1) ^ (0x1d5 & 0xFF));
                } else {
                    remainder = (byte) (remainder << 1);
                }
            }

            _crc_table[i] = remainder;
        }
    }

    public static byte crc_update(byte crc, byte crc_byte)
    {
        return _crc_table[(crc ^ crc_byte) & 0xFF];
    }

}
