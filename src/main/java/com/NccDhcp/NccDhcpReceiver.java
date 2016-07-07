package com.NccDhcp;

import com.Ncc;
import com.NccPools.NccPoolData;
import com.NccPools.NccPools;
import com.NccSystem.NccUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

class NccDhcpReceiver extends Thread {

    private static Logger logger = Logger.getLogger(NccDhcpServer.class);
    InetAddress localIP;
    private byte[] recv;
    private DatagramPacket inPkt;
    private DatagramSocket dhcpSocket;
    private Long nullIP;

    NccDhcpReceiver(byte[] recv, DatagramPacket inPkt, DatagramSocket dhcpSocket, InetAddress localIP) {
        this.localIP = localIP;
        this.recv = recv;
        this.inPkt = inPkt;
        this.dhcpSocket = dhcpSocket;

        this.nullIP = null;
        try {
            this.nullIP = NccUtils.ip2long("0.0.0.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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

            try {
                NccDhcpRelayAgentData agent = new NccDhcpRelayAgent().getRelayAgentByIP(relayAgent);

                if (agent == null) {
                    if (Ncc.dhcpLogLevel >= 5) {
                        try {
                            logger.info("Request from unknown RelayAgent: " + NccUtils.long2ip(relayAgent));
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }

                if (!remoteID.equals(""))
                    new NccDhcpBinding().setUnbinded(remoteID, circuitID, clientMAC, relayAgent);

            } catch (NccDhcpRelayAgentException e) {
                e.printStackTrace();
            }

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
            logger.info("Send " + pkt.type2string(type) + " to " + inPkt.getAddress().getHostAddress() + ":" + inPkt.getPort() + " clientMAC: " + pkt.getClientMAC() + " IP=" + ip.getHostAddress() + " localIP=" + this.localIP.getHostAddress());

        try {
            DatagramPacket outPkt = new DatagramPacket(dhcpReply, dhcpReply.length, inPkt.getAddress(), inPkt.getPort());
            dhcpSocket.send(outPkt);

            NccDhcpServer.requestProcessed++;

            return outPkt;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void requestInform(NccDhcpPacket pkt){
        if (Ncc.dhcpLogLevel >= 5)
            logger.info("DHCPINFORM from " + inPkt.getAddress().getHostAddress() + " clientMAC: " + pkt.getClientMAC());
        if (Ncc.dhcpLogLevel >= 5)
            logger.info("RelayAgent: " + pkt.getRelayAgent().getHostAddress() + " remoteID: " + pkt.getOpt82RemoteID() + " circuitID: " + pkt.getOpt82CircuitID() + " clientID: " + pkt.getClientID());
    }

    private void requestDiscover(NccDhcpPacket pkt){

        if (Ncc.dhcpLogLevel >= 5)
            logger.info("DHCPDISCOVER from " + inPkt.getAddress().getHostAddress() + " clientMAC: " + pkt.getClientMAC());

        if (Ncc.dhcpLogLevel >= 5)
            logger.info("RelayAgent: " + pkt.getRelayAgent().getHostAddress() + " remoteID: " + pkt.getOpt82RemoteID() + " circuitID: " + pkt.getOpt82CircuitID() + " clientMAC: " + pkt.getClientMAC());

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
                logger.info("Empty remoteID in DHCPDISCOVER clientMAC: " + pkt.getClientMAC());
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

            if (Ncc.dhcpLogLevel >= 6) {
                logger.info("Lease not found for clientMAC=" + clientMAC);
            }

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
                            try {
                                leaseData = new NccDhcpLeases().allocateLease(uid, poolData, clientMAC, remoteID, circuitID, NccUtils.ip2long(agentIP.getHostAddress()), pkt.ba2int(pkt.dhcpTransID));
                            } catch (NccDhcpException e) {
                                e.printStackTrace();
                            }

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

    private void requestRequest(NccDhcpPacket pkt){
        if (Ncc.dhcpLogLevel >= 5)
            logger.info("DHCPREQUEST from " + inPkt.getAddress().getHostAddress() + " clientMAC: " + pkt.getClientMAC());

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

    private void requestRelease(NccDhcpPacket pkt){
        if (Ncc.dhcpLogLevel >= 5)
            logger.info("DHCPRELEASE from " + inPkt.getAddress().getHostAddress() + " clientMAC: " + pkt.getClientID());
        if (Ncc.dhcpLogLevel >= 5)
            logger.info("RelayAgent: " + pkt.getRelayAgent().getHostAddress() + " remoteID: " + pkt.getOpt82RemoteID() + " circuitID: " + pkt.getOpt82CircuitID() + " clientID: " + pkt.getClientID());
    }

    @Override
    public void run() {

        setName("NccDhcpReceiver");

        try {
            NccDhcpPacket pkt = new NccDhcpPacket(recv, inPkt.getLength());

            if (Ncc.dhcpIgnoreBroadcast && inPkt.getAddress().getHostAddress().equals("0.0.0.0")) {
                if (Ncc.dhcpLogLevel >= 6)
                    logger.info("DHCP broadcast packet ignored");
                return;
            }

            if (pkt.getType() == NccDhcpPacket.DHCP_MSG_TYPE_INFORM) {
                requestInform(pkt);
            }

            if (pkt.getType() == NccDhcpPacket.DHCP_MSG_TYPE_DISCOVER) {
                requestDiscover(pkt);
            }

            if (pkt.getType() == NccDhcpPacket.DHCP_MSG_TYPE_REQUEST) {
                requestRequest(pkt);
            }

            if (pkt.getType() == NccDhcpPacket.DHCP_MSG_TYPE_RELEASE) {
                requestRelease(pkt);
            }

        } catch (NccDhcpException e) {
            e.printStackTrace();
        }
    }
}
