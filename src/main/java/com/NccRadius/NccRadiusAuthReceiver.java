package com.NccRadius;

import org.apache.log4j.Logger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class NccRadiusAuthReceiver extends Thread {
    private static Logger logger = Logger.getLogger("RadiusLogger");

    NccRadiusAuthReceiver(byte[] recv, DatagramPacket inPkt, DatagramSocket socket){
    }

    @Override
    public void run(){

    }
}
