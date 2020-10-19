package com.example.quadrupel_ble;

public class Fix {

    public static final int FIXED_POINT = 16;
    public static final int ONE = 1 << FIXED_POINT;

    public static int mul(int a, int b) {
        return (int) ((long) a * (long) b >> FIXED_POINT);
    }

    public static int toFix( double val ) {
        return (int) (val * ONE);
    }

    public static int intVal( int fix ) {
        return fix >> FIXED_POINT;
    }

    public static double doubleVal( int fix ) {
        return ((double) fix) / ONE;
    }
}
