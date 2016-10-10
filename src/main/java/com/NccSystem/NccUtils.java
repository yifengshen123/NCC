package com.NccSystem;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class NccUtils {
    public static Long ip2long(String stringIp) {
        long result = 0;

        String[] ipAddressInArray = stringIp.split("\\.");

        if (ipAddressInArray.length < 4) return null;

        for (int i = 3; i >= 0; i--) {

            long ip = 0;
            try {
                ip = Long.parseLong(ipAddressInArray[3 - i]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
            result |= ip << (i * 8);
        }

        return result;
    }

    public static String long2ip(long longIp) {
        ByteBuffer bb = ByteBuffer.allocate(4).putInt((int) longIp);
        InetAddress address = null;
        try {
            address = InetAddress.getByAddress(bb.array());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
        return address.getHostAddress();
    }
}
