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
    private InetAddress localIP;

    private static DatagramSocket dhcpSocket;

    public NccDhcpServer() {
        try {
            this.dhcpSocket = new DatagramSocket(67);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void start(final InetAddress localIP) {

        this.localIP = localIP;

        Thread dhcpWatchThread = new Thread(new Runnable() {
            @Override
            public void run() {

                class NccDhcpTimer extends TimerTask {
                    @Override
                    public void run() {
                        NccDhcpLeases leases = new NccDhcpLeases();
                        NccDhcpBinding binding = new NccDhcpBinding();
                        leases.cleanupLeases();
                        binding.cleanupBinding();
                    }
                }

                Timer dhcpTimer = new Timer();
                dhcpTimer.schedule(new NccDhcpTimer(), 1000, Ncc.dhcpTimer * 1000);
            }
        });
        dhcpWatchThread.start();

        Thread dhcpMainThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        final byte[] recv = new byte[512];

                        final DatagramPacket inPkt = new DatagramPacket(recv, recv.length);
                        dhcpSocket.receive(inPkt);

                        class ReceiveRunnable implements Runnable {

                            InetAddress localIP;

                            ReceiveRunnable(InetAddress localIP) {
                                this.localIP = localIP;
                            }

                            NccDhcpBindData checkBind(String remoteID, String circuitID, String clientMAC, Long relayAgent) {
                                NccDhcpBindData bindData = new NccDhcpBinding().getBinding(remoteID, circuitID, clientMAC, relayAgent);

                                if (bindData != null) {
                                    if (Ncc.dhcpLogLevel >= 6) logger.info("User binded: uid=" + bindData.uid);
                                    return bindData;
                                } else {
                                    try {
                                        if (Ncc.dhcpLogLevel >= 5)
                                            logger.info("Unbinded user: remoteID=" + remoteID + " circuitID=" + circuitID + " clientMAC=" + clientMAC + " relayAgent=" + NccUtils.long2ip(relayAgent));
                                    } catch (UnknownHostException e) {
                                        e.printStackTrace();
                                    }
                                    if (!remoteID.equals(""))
                                        new NccDhcpBinding().setUnbinded(remoteID, circuitID, clientMAC, relayAgent);
                                    return null;
                                }
                            }

                            DatagramPacket sendReply(byte type, Long localIP, Long leaseIP, Long leaseNetmask, Long leaseRouter, Long leaseDNS1, Long leaseDNS2, Long leaseNextServer, int leaseTime) {

                                NccDhcpPacket pkt = null;
                                try {
                                    pkt = new NccDhcpPacket(recv, inPkt.getLength());
                                } catch (NccDhcpException e) {
                                    e.printStackTrace();
                                }

                                InetAddress ip = null;
                                InetAddress netmask = null;
                                InetAddress router = null;
                                InetAddress dns1 = null;
                                InetAddress dns2 = null;
                                InetAddress nextserver = null;

                                try {
                                    ip = InetAddress.getByName(NccUtils.long2ip(leaseIP));
                                    netmask = InetAddress.getByName(NccUtils.long2ip(leaseNetmask));
                                    router = InetAddress.getByName(NccUtils.long2ip(leaseRouter));
                                    if (leaseDNS1 != null) dns1 = InetAddress.getByName(NccUtils.long2ip(leaseDNS1));
                                    if (leaseDNS2 != null) dns2 = InetAddress.getByName(NccUtils.long2ip(leaseDNS2));
                                    if (leaseNextServer != null)
                                        nextserver = InetAddress.getByName(NccUtils.long2ip(leaseNextServer));
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                }

                                byte[] dhcpReply = null;

                                dhcpReply = pkt.buildReply(type, this.localIP, ip, netmask, router, dns1, dns2, nextserver, leaseTime);

                                if (Ncc.dhcpLogLevel >= 5)
                                    logger.info("Send " + pkt.type2string(type) + " to " + inPkt.getAddress().getHostAddress() + ":" + inPkt.getPort() + " clientMAC: " + pkt.getClientID() + " IP=" + ip.getHostAddress() + " localIP=" + this.localIP.getHostAddress());

                                try {
                                    DatagramPacket outPkt = new DatagramPacket(dhcpReply, dhcpReply.length, inPkt.getAddress(), 67);
                                    dhcpSocket.send(outPkt);

                                    return outPkt;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                return null;
                            }

                            @Override
                            public void run() {

                                try {
                                    NccDhcpPacket pkt = new NccDhcpPacket(recv, inPkt.getLength());

                                    if(Ncc.dhcpIgnoreBroadcast && inPkt.getAddress().getHostAddress().equals("0.0.0.0")){
                                        if(Ncc.dhcpLogLevel >=6)
                                            logger.info("DHCP broadcast packet ignored");
                                        return;
                                    }

                                    Long nullIP = null;
                                    try {
                                        nullIP = NccUtils.ip2long("0.0.0.0");
                                    } catch (UnknownHostException e) {
                                        e.printStackTrace();
                                    }

                                    if (pkt.getType() == NccDhcpPacket.DHCP_MSG_TYPE_INFORM) {
                                        if (Ncc.dhcpLogLevel >= 5)
                                            logger.info("DHCPINFORM from " + inPkt.getAddress().getHostAddress() + " clientMAC: " + pkt.getClientID());
                                        if (Ncc.dhcpLogLevel >= 5)
                                            logger.info("RelayAgent: " + pkt.getRelayAgent().getHostAddress() + " remoteID: " + pkt.getOpt82RemoteID() + " circuitID: " + pkt.getOpt82CircuitID() + " clientID: " + pkt.getClientID());
                                    }

                                    if (pkt.getType() == NccDhcpPacket.DHCP_MSG_TYPE_DISCOVER) {

                                        if (Ncc.dhcpLogLevel >= 5)
                                            logger.info("DHCPDISCOVER from " + inPkt.getAddress().getHostAddress() + " clientMAC: " + pkt.getClientID());

                                        if (Ncc.dhcpLogLevel >= 5)
                                            logger.info("RelayAgent: " + pkt.getRelayAgent().getHostAddress() + " remoteID: " + pkt.getOpt82RemoteID() + " circuitID: " + pkt.getOpt82CircuitID() + " clientID: " + pkt.getClientID());

                                        // TODO: 4/20/16 set real local IP of outgoing iface
                                        InetAddress localIP = null;
                                        try {
                                            localIP = InetAddress.getByName("151.0.48.86");
                                        } catch (UnknownHostException e) {
                                            e.printStackTrace();
                                        }

                                        InetAddress agentIP = pkt.getRelayAgent();
                                        String clientMAC = pkt.getClientMAC();
                                        String remoteID = pkt.getOpt82RemoteID();
                                        String circuitID = pkt.getOpt82CircuitID();
                                        Long relayAgent = null;
                                        try {
                                            relayAgent = NccUtils.ip2long(agentIP.getHostAddress());
                                        } catch (UnknownHostException e) {
                                            e.printStackTrace();
                                        }

                                        if (remoteID.equals("")) {
                                            if (Ncc.dhcpLogLevel >= 6)
                                                logger.info("Empty remoteID in DHCPDISCOVER clientMAC: " + pkt.getClientID());
                                            try {
                                                sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, NccUtils.ip2long(localIP.getHostAddress()), nullIP, nullIP, nullIP, nullIP, nullIP, nullIP, 0);
                                            } catch (UnknownHostException e) {
                                                e.printStackTrace();
                                            }
                                            return;
                                        }

                                        NccDhcpBindData bindData = checkBind(remoteID, circuitID, clientMAC, relayAgent);

                                        NccDhcpLeaseData leaseData = new NccDhcpLeases().getLeaseByMAC(relayAgent, circuitID, clientMAC, pkt.ba2int(pkt.dhcpTransID));

                                        if (leaseData != null) {

                                            if (Ncc.dhcpLogLevel >= 6)
                                                logger.info("Found lease for " + relayAgent.toString() + " " + clientMAC);

                                            NccPoolData poolData = new NccPools().getPool(leaseData.leasePool);

                                            try {
                                                sendReply(NccDhcpPacket.DHCP_MSG_TYPE_OFFER, NccUtils.ip2long(localIP.getHostAddress()), leaseData.leaseIP, leaseData.leaseNetmask, leaseData.leaseRouter, leaseData.leaseDNS1, leaseData.leaseDNS2, leaseData.leaseNextServer, poolData.poolLeaseTime);
                                                return;
                                            } catch (UnknownHostException e) {
                                                e.printStackTrace();
                                            }
                                        } else {

                                            try {
                                                NccDhcpRelayAgentData agentData = null;
                                                try {
                                                    agentData = new NccDhcpRelayAgent().getRelayAgentByIP(NccUtils.ip2long(agentIP.getHostAddress()));

                                                    if (agentData != null) {

                                                        Integer pool;
                                                        Integer uid;

                                                        if (bindData != null) {
                                                            pool = agentData.agentPool;
                                                            uid = bindData.uid;
                                                        } else {
                                                            pool = agentData.agentUnbindedPool;
                                                            uid = 0;
                                                        }

                                                        NccPoolData poolData = new NccPools().getPool(pool);

                                                        if (poolData != null) {
                                                            leaseData = new NccDhcpLeases().allocateLease(uid, poolData, clientMAC, remoteID, circuitID, NccUtils.ip2long(agentIP.getHostAddress()), pkt.ba2int(pkt.dhcpTransID));

                                                            if (leaseData != null) {
                                                                sendReply(NccDhcpPacket.DHCP_MSG_TYPE_OFFER, NccUtils.ip2long(localIP.getHostAddress()), leaseData.leaseIP, leaseData.leaseNetmask, leaseData.leaseRouter, leaseData.leaseDNS1, leaseData.leaseDNS2, leaseData.leaseNextServer, poolData.poolLeaseTime);
                                                                new NccDhcpLeases().renewLease(leaseData);
                                                                return;
                                                            } else {
                                                                logger.error("Can't allocate lease");
                                                                sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, NccUtils.ip2long(localIP.getHostAddress()), nullIP, nullIP, nullIP, nullIP, nullIP, nullIP, 0);
                                                                return;
                                                            }
                                                        } else {
                                                            logger.error("Pool for relay agent " + NccUtils.long2ip(relayAgent) + " not found");
                                                            sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, NccUtils.ip2long(localIP.getHostAddress()), nullIP, nullIP, nullIP, nullIP, nullIP, nullIP, 0);
                                                            return;
                                                        }
                                                    } else {
                                                        logger.error("Relay agent " + NccUtils.long2ip(relayAgent) + " not found");
                                                    }
                                                } catch (NccDhcpRelayAgentException e) {
                                                    e.printStackTrace();
                                                }

                                                sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, NccUtils.ip2long(localIP.getHostAddress()), nullIP, nullIP, nullIP, nullIP, nullIP, nullIP, 0);
                                                return;
                                            } catch (UnknownHostException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    if (pkt.getType() == NccDhcpPacket.DHCP_MSG_TYPE_REQUEST) {

                                        if (Ncc.dhcpLogLevel >= 5)
                                            logger.info("DHCPREQUEST from " + inPkt.getAddress().getHostAddress() + " clientMAC: " + pkt.getClientID());

                                        if (Ncc.dhcpLogLevel >= 5)
                                            logger.info("RelayAgent: " + pkt.getRelayAgent().getHostAddress() + " remoteID: " + pkt.getOpt82RemoteID() + " circuitID: " + pkt.getOpt82CircuitID() + " clientID: " + pkt.getClientID());

                                        InetAddress agentIP = pkt.getRelayAgent();
                                        String clientMAC = pkt.getClientMAC();
                                        String remoteID = pkt.getOpt82RemoteID();
                                        String circuitID = pkt.getOpt82CircuitID();

                                        if (remoteID.equals("")) {
                                            if (Ncc.dhcpLogLevel >= 6)
                                                logger.info("Empty remoteID clientMAC: " + pkt.getClientID());
                                        }
                                        // TODO: 4/20/16 set real local IP of outgoing iface
                                        InetAddress localIP = null;
                                        try {
                                            localIP = InetAddress.getByName("151.0.48.86");
                                        } catch (UnknownHostException e) {
                                            e.printStackTrace();
                                        }

                                        Long relayAgent = null;
                                        try {
                                            relayAgent = NccUtils.ip2long(agentIP.getHostAddress());
                                        } catch (UnknownHostException e) {
                                            e.printStackTrace();
                                        }

                                        InetAddress clientIP = pkt.getClientIPAddress();
                                        InetAddress reqIP = pkt.getAddressRequest();

                                        NccDhcpLeaseData leaseData = null;

                                        if (!clientIP.getHostAddress().equals("0.0.0.0")) {     // renew lease

                                            if (Ncc.dhcpLogLevel >= 6)
                                                logger.info("Lease RENEW clientMAC: " + pkt.getClientID());

                                            leaseData = new NccDhcpLeases().getLeaseByMAC(relayAgent, circuitID, clientMAC, pkt.ba2int(pkt.dhcpTransID));

                                            if (leaseData != null) {

                                                try {
                                                    if (Ncc.dhcpLogLevel >= 6)
                                                        logger.info("Found lease for " + NccUtils.long2ip(relayAgent) + " " + clientMAC);
                                                } catch (UnknownHostException e) {
                                                    e.printStackTrace();
                                                    e.printStackTrace();
                                                }

                                                NccPoolData poolData = new NccPools().getPool(leaseData.leasePool);

                                                if (poolData != null) {
                                                    try {
                                                        new NccDhcpLeases().renewLease(leaseData);
                                                        sendReply(NccDhcpPacket.DHCP_MSG_TYPE_ACK, NccUtils.ip2long(localIP.getHostAddress()), leaseData.leaseIP, leaseData.leaseNetmask, leaseData.leaseRouter, leaseData.leaseDNS1, leaseData.leaseDNS2, leaseData.leaseNextServer, poolData.poolLeaseTime);
                                                        return;
                                                    } catch (UnknownHostException e) {
                                                        e.printStackTrace();
                                                    }

                                                } else {
                                                    logger.error("Pool " + leaseData.leasePool + " not found");
                                                    try {
                                                        sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, NccUtils.ip2long(localIP.getHostAddress()), nullIP, nullIP, nullIP, nullIP, nullIP, nullIP, 0);
                                                        return;
                                                    } catch (UnknownHostException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return;
                                                }

                                            } else {
                                                logger.error("Lease for " + clientMAC + " not found");
                                                try {
                                                    sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, NccUtils.ip2long(localIP.getHostAddress()), nullIP, nullIP, nullIP, nullIP, nullIP, nullIP, 0);
                                                    return;
                                                } catch (UnknownHostException e) {
                                                    e.printStackTrace();
                                                }
                                                return;
                                            }
                                        } else if (reqIP != null) { // accept new lease

                                            if (Ncc.dhcpLogLevel >= 6)
                                                logger.info("Lease ACCEPT clientMAC: " + pkt.getClientID());

                                            NccDhcpBindData bindData = checkBind(remoteID, circuitID, clientMAC, relayAgent);

                                            try {
                                                leaseData = new NccDhcpLeases().acceptLease(NccUtils.ip2long(reqIP.getHostAddress()), clientMAC, remoteID, circuitID, pkt.ba2int(pkt.dhcpTransID));

                                                if (leaseData != null) {

                                                    NccPoolData poolData = new NccPools().getPool(leaseData.leasePool);

                                                    try {
                                                        sendReply(NccDhcpPacket.DHCP_MSG_TYPE_ACK, NccUtils.ip2long(localIP.getHostAddress()), leaseData.leaseIP, leaseData.leaseNetmask, leaseData.leaseRouter, leaseData.leaseDNS1, leaseData.leaseDNS2, leaseData.leaseNextServer, poolData.poolLeaseTime);
                                                        return;
                                                    } catch (UnknownHostException e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    logger.error("Lease not found for " + clientMAC);
                                                    try {
                                                        sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, NccUtils.ip2long(localIP.getHostAddress()), nullIP, nullIP, nullIP, nullIP, nullIP, nullIP, 0);
                                                        return;
                                                    } catch (UnknownHostException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } catch (UnknownHostException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }

                                    if (pkt.getType() == NccDhcpPacket.DHCP_MSG_TYPE_RELEASE) {

                                        if (Ncc.dhcpLogLevel >= 5)
                                            logger.info("DHCPRELEASE from " + inPkt.getAddress().getHostAddress() + " clientMAC: " + pkt.getClientID());
                                        if (Ncc.dhcpLogLevel >= 5)
                                            logger.info("RelayAgent: " + pkt.getRelayAgent().getHostAddress() + " remoteID: " + pkt.getOpt82RemoteID() + " circuitID: " + pkt.getOpt82CircuitID() + " clientID: " + pkt.getClientID());
                                    }
                                } catch (NccDhcpException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        ;

                        Thread dhcpReceiveThread = new Thread(new ReceiveRunnable(localIP));

                        dhcpReceiveThread.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        );

        dhcpMainThread.start();
    }
}
