package com.example.quadrupel_ble;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static com.example.quadrupel_ble.Protocol.State.*;

public class Protocol {

    public static enum State {
        GET_START,
        GET_LEN,
        GET_TYPE,
        GET_DATA,
        GET_CRC,
    }

    public static byte START_BYTE = (byte) 0xFF;
    public static int BASE_PACKET_SIZE = 4;
    public static int MAX_PKT_LEN = 192;
    public static State state = State.GET_START;

    public static int data_offset = 0;
    public static int crc = 0;

    public enum modes {
        MODE_SAFE(0x00),
        MODE_PANIC(0x01),
        MODE_MANUAL(0x02),

        MODE_CALIB(0x03),
        MODE_YAW(0x04),
        MODE_FULL(0x05),

        UNSET_RAW_SENSORS(0x04),
        UNSET_HEIGHT_CTL (0x05),
        UNSET_WIRELESS(0x06),

        SET_RAW_SENSORS (0x07),
        SET_HEIGHT_CTL (0x08),
        SET_WIRELESS (0x09);

        public final int label;
        private static Map map = new HashMap<>();

        private modes(int label) {
            this.label = label;
        }

        static {
            for (modes pageType : modes.values()) {
                map.put(pageType.label, pageType);
            }
        }

        public static modes valueOf(int mode) {
            return (modes) map.get(mode);
        }

        public int getValue() {
            return label;
        }

        public byte toByte(){
            return (byte) label;
        }
    }

    public enum packet_type {
        ACK(0x00),
        NACK(0x01),

        PING(0x02),
        STOP(0x03),
        RESET(0x04),
        MODE_SWITCH (0x05),

        CTRL(0x06),
        PIDCOEFS (0x07),
        PYRLCOEFS  (0x08),

        LOGSS (0x09),
        LOGM (0x08),
        LOGR  (0x09),
        LOGD  (0x10),

        /* Append any extra packet types here, so they'll be easier to remove if we
         * decide to remove them in the future */
        NONE  (0x0d); //It is useful and used, --Z

        public final int label;
        private static Map map = new HashMap<>();

        private packet_type(int label) {
            this.label = label;
        }

        static {
            for (packet_type pageType : packet_type.values()) {
                map.put(pageType.label, pageType);
            }
        }

        public static packet_type valueOf(int packetType) {
            return (packet_type) map.get(packetType);
        }

        public int getValue() {
            return label;
        }

        public byte toByte(){
            return (byte) label;
        }
    }



   // static byte compute_crc(Packet p);

    static void protocol_init() {
        CRC.crc_init();
    }

    public static byte[] assemble_packet(Packet packet) {
        packet_type type = packet.type;
        byte[] data = packet.data.raw;
        byte data_size = packet.len;
        byte[] data_buffer = new byte[data_size + BASE_PACKET_SIZE];

        data_buffer[0] = START_BYTE;
        data_buffer[1] = type.toByte();
        data_buffer[2] = data_size;


        if (data_size != 0) {
            //fprintf(stderr, "\nCopy data into buffer\n");
            for (int i = 0; i < data_size; i++) {
                data_buffer[(BASE_PACKET_SIZE - 1) + i] = data[i];
            }
        } else {
            //fprintf(stderr, "\nNo data copied\n");
        }


        //fprintf(stderr, "\nAdd CRC\n");

        byte crc_val = 0;
        for (int i = 1; i < data_size + BASE_PACKET_SIZE - 1; i++)//skip byte 0, which is start byte, until data + size - 1
        {
            crc_val = CRC.crc_update(crc_val, data_buffer[i]);
        }
        //fprintf(stderr, "CRC : %x\n", crc_val);
        data_buffer[data_size + 3] = crc_val;//last index (4 + data length) - 1
        return data_buffer;
    }

    /* WCET: ~50us if no printf gets executed, else up to 1ms */
    public static int protocol_parse(byte packet_byte, Packet pkt) {
        int retval = 0;

        switch(state)
        {
            case GET_START:
                /* Reject all bytes until a START byte is received */
                if ( packet_byte ==START_BYTE){
                crc = 0;
                pkt.crc = 0;
                pkt.len = 0;
                pkt.type = packet_type.ACK;//0

                state = GET_TYPE;
            } else{
                retval = -2;
            }
            break;

            case GET_TYPE:
                /* Validate the type of the packet */
                if (packet_byte >= packet_type.values().length){
                retval = -1;
                state = GET_START;
            } else{
                pkt.type = packet_type.valueOf(packet_byte);
                crc = CRC.crc_update((byte) crc, packet_byte);
                state = GET_LEN;
            }
            break;

            case GET_LEN:
                /* Validate the length of the packet */
                if ( packet_byte > MAX_PKT_LEN){
                retval = -1;
                state = GET_START;
            } else{
                data_offset = 0;
                pkt.len = packet_byte ;
                crc = CRC.crc_update((byte) crc, packet_byte);
                /* If the length is 0, the next byte is the CRC */
                if (pkt.len > 0) {
                    state = GET_DATA;
                } else {
                    state = GET_CRC;
                }
            }
            break;

            case GET_DATA:
                /* Get data until the specified length */
                pkt.data.raw[data_offset++] = packet_byte;
                crc = CRC.crc_update((byte) crc, packet_byte);

                if (data_offset >= pkt.len) {
                    state = GET_CRC;
                }
                break;

            case GET_CRC:
                pkt.crc = packet_byte;

                /* Validate the CRC, notify the caller if invalid */
                if ( packet_byte !=crc){
                retval = -1;
            } else{
                retval = 1;
            }
            state = GET_START;
            break;

            default:
                Log.i("Protocol", "[protocol.c] Invalid state detected");
                state = GET_START;
                break;
        }

            return retval;
    }

    /**
     * Packet assembly functions, delete once upstream has proper ones {{{
     */
/*void
protocol_build_ack(Packet *dst, uint8_t mode)
{
	dst->type = ACK;
	dst->len = sizeof(AckData);
	dst->data.ack.mode = mode;
	dst->crc = compute_crc(dst);
}*/


    /**
     * Compute the CRC of a fully-populated Packet structure
     *
     * @param p the packet of which the CRC should be computed
     */
    static byte compute_crc(Packet p) {
        int i;
        byte crc = 0;

        crc = CRC.crc_update(crc, p.type.toByte());
        crc = CRC.crc_update(crc, p.len);
        for (i = 0; i < p.len; i++) {
            crc = CRC.crc_update(crc, p.data.raw[i]);
        }

        return crc;
    }
}
/* }}} */
