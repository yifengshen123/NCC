package com.NccDhcp;

import com.Ncc;
import com.NccPools.NccPoolData;
import com.NccPools.NccPools;
import com.NccSystem.NccUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class NccDhcpServer {
    private static Logger logger = Logger.getLogger(NccDhcpServer.class);
    private static InetAddress localIP;
    private static Integer port = 67;
    private static DatagramSocket dhcpSocket;

    public static Long requestProcessed = 0L;

    public NccDhcpServer(InetAddress localIP) {
        this.localIP = localIP;
    }

    public NccDhcpServer(InetAddress localIP, Integer port) {
        this.localIP = localIP;
        this.port = port;
    }

    public void start() {

        try {
            this.dhcpSocket = new DatagramSocket(this.port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        new Thread() {
            public void run() {

                setName("NccDhcp timer");

                class NccDhcpTimer extends TimerTask {
                    @Override
                    public void run() {
                        NccDhcpLeases leases = new NccDhcpLeases();
                        NccDhcpBinding binding = new NccDhcpBinding();
                        leases.cleanupLeases();
                        binding.cleanupBinding();

                        if (Ncc.dhcpLogLevel >= 7)
                            logger.info("Request rate: " + requestProcessed + " req/sec");
                        requestProcessed = 0L;

                        if(Ncc.nccForceGC){
                            System.gc();
                        }

                    }
                }

                Timer dhcpTimer = new Timer();
                dhcpTimer.schedule(new NccDhcpTimer(), 1000, Ncc.dhcpTimer * 1000);
            }
        }.start();
        logger.info("Started NccDhcp scheduler");

        new Thread() {
            public void run() {

                setName("NccDhcp listener");
                while (true) {
                    try {
                        final byte[] recv = new byte[512];

                        final DatagramPacket inPkt = new DatagramPacket(recv, recv.length);
                        dhcpSocket.receive(inPkt);

                        NccDhcpReceiver dhcpReceiver = new NccDhcpReceiver(recv, inPkt, dhcpSocket, localIP);
                        dhcpReceiver.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        logger.info("NccDhcp started on " + localIP.getHostAddress() + ":" + port);

    }
}
