package com.example.pratik.bluetoothdata;

public class BytesTrans {

    public static long[] bytes2HexString(byte[] b, int count) {
        long  receivedData[] =  new long[29];
        receivedData[0] = 82;
        String ret = "";
        for (int i = 0; i < count; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() ==1) {
                hex = '0' + hex;
            }
            String capsHex= hex.toUpperCase();

            receivedData[i+1] = Long.parseLong(capsHex,16);

            ret += hex.toUpperCase() + " ";
        }
        return receivedData;
    }
}
