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
    private InetAddress nullIP;

    NccDhcpReceiver(byte[] recv, DatagramPacket inPkt, DatagramSocket dhcpSocket, InetAddress localIP) {
        this.localIP = localIP;
        this.recv = recv;
        this.inPkt = inPkt;
        this.dhcpSocket = dhcpSocket;

        this.nullIP = null;
        try {
            this.nullIP = InetAddress.getByName("0.0.0.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    NccDhcpBindData checkBind(NccDhcpRequest request) {
        NccDhcpBindData bindData = new NccDhcpBinding().getBinding(request);

        if (bindData != null) {
            if (Ncc.dhcpLogLevel >= 6) logger.info("User binded: uid=" + bindData.uid);
            return bindData;
        } else {
            if (Ncc.dhcpLogLevel >= 5)
                logger.info("Unbinded user: remoteID='" + request.getRemoteID() + "' circuitID='" + request.getCircuitID() + "' clientMAC='" + request.getClientMAC() + "' relayAgent='" + request.getRelayAgentName() + "'");

            try {
                NccDhcpRelayAgentData agent = new NccDhcpRelayAgent().getRelayAgentByIP(request.getRelayAgent());

                if (agent == null) {
                    if (Ncc.dhcpLogLevel >= 5) {
                        logger.info("Request from unknown relayAgent='" + request.getRelayAgentName() + "'");
                        return null;
                    }
                }

                if (!request.getRemoteID().equals(""))
                    new NccDhcpBinding().setUnbinded(request);

            } catch (NccDhcpRelayAgentException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    DatagramPacket sendReply(byte type, NccDhcpLeaseData leaseData, int leaseTime) {

        NccDhcpPacket pkt = null;
        try {
            pkt = new NccDhcpPacket(recv, inPkt.getLength());
        } catch (NccDhcpException e) {
            e.printStackTrace();
        }

        InetAddress ip = nullIP;
        InetAddress netmask = nullIP;
        InetAddress router = nullIP;
        InetAddress dns1 = nullIP;
        InetAddress dns2 = nullIP;
        InetAddress nextserver = nullIP;

        if (leaseData != null) {
            try {
                ip = InetAddress.getByName(NccUtils.long2ip(leaseData.leaseIP));
                netmask = InetAddress.getByName(NccUtils.long2ip(leaseData.leaseNetmask));
                router = InetAddress.getByName(NccUtils.long2ip(leaseData.leaseRouter));
                if (leaseData.leaseDNS1 != null) dns1 = InetAddress.getByName(NccUtils.long2ip(leaseData.leaseDNS1));
                if (leaseData.leaseDNS2 != null) dns2 = InetAddress.getByName(NccUtils.long2ip(leaseData.leaseDNS2));
                if (leaseData.leaseNextServer != null)
                    nextserver = InetAddress.getByName(NccUtils.long2ip(leaseData.leaseNextServer));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        byte[] dhcpReply = null;

        if (Ncc.dhcpLogLevel >= 5)
            logger.info("Trying to send " + pkt.type2string(type) + " to " + inPkt.getAddress().getHostAddress() + ":" + inPkt.getPort() + " clientMAC='" + pkt.getClientMAC() + "' IP='" + ip.getHostAddress() + "' localIP='" + this.localIP.getHostAddress() + "'");

        dhcpReply = pkt.buildReply(type, this.localIP, ip, netmask, router, dns1, dns2, nextserver, leaseTime);

        if (Ncc.dhcpLogLevel >= 5)
            logger.info("Sent " + pkt.type2string(type) + " to " + inPkt.getAddress().getHostAddress() + ":" + inPkt.getPort() + " clientMAC='" + pkt.getClientMAC() + "' IP='" + ip.getHostAddress() + "' localIP='" + this.localIP.getHostAddress() + "'");

        try {
            DatagramPacket outPkt = new DatagramPacket(dhcpReply, dhcpReply.length, inPkt.getAddress(), inPkt.getPort());
            dhcpSocket.send(outPkt);

            // send copy to port 67 for D-Link
            if(inPkt.getPort()!=67) {
                outPkt = new DatagramPacket(dhcpReply, dhcpReply.length, inPkt.getAddress(), 67);
                dhcpSocket.send(outPkt);
            }

            NccDhcpServer.requestProcessed++;

            return outPkt;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void logRequest(NccDhcpPacket pkt) {
        String type = "";

        switch (pkt.getType()) {
            case NccDhcpPacket.DHCP_MSG_TYPE_DISCOVER:
                type = "DHCPDISCOVER";
                break;
            case NccDhcpPacket.DHCP_MSG_TYPE_REQUEST:
                type = "DHCPREQUEST";
                break;
            case NccDhcpPacket.DHCP_MSG_TYPE_INFORM:
                type = "DHCPINFORM";
                break;
            case NccDhcpPacket.DHCP_MSG_TYPE_RELEASE:
                type = "DHCPRELEASE";
                break;
            case NccDhcpPacket.DHCP_MSG_TYPE_DECLINE:
                type = "DHCPDECLINE";
                break;
            default:
                break;
        }
        logger.info(type + " from '" + inPkt.getAddress().getHostAddress() + "' clientMAC='" + pkt.getClientMAC() + "'");
        logger.info(
                "RelayAgent='" + pkt.getRelayAgent().getHostAddress() + "' " +
                        "remoteID='" + pkt.getOpt82RemoteID() + "' " +
                        "circuitID='" + pkt.getOpt82CircuitID() + "' " +
                        "clientID='" + pkt.getClientID() + "' " +
                        "clientIP='" + pkt.getClientIPAddress().getHostAddress() + "'");
    }

    private void requestInform(NccDhcpPacket pkt) {
        if (Ncc.dhcpLogLevel >= 5) logRequest(pkt);
    }

    private void requestDiscover(NccDhcpPacket pkt) {

        if (Ncc.dhcpLogLevel >= 5) logRequest(pkt);

        NccDhcpRequest request = new NccDhcpRequest(pkt);

        if (request.getRemoteID().equals("")) {
            if (Ncc.dhcpLogLevel >= 6)
                logger.info("Empty remoteID in DHCPDISCOVER clientMAC='" + pkt.getClientMAC() + "'");

            sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
            return;
        }

        NccDhcpBindData bindData = checkBind(request);

        NccDhcpLeaseData leaseData = new NccDhcpLeases().getLeaseByRequest(request);

        if (leaseData != null) {

            if (Ncc.dhcpLogLevel >= 6)
                logger.info("Found lease for relayAgent='" + request.getRelayAgentName() + "' clientMAC='" + request.getClientMAC() + "'");

            NccPoolData poolData = new NccPools().getPool(leaseData.leasePool);

            sendReply(NccDhcpPacket.DHCP_MSG_TYPE_OFFER, leaseData, poolData.poolLeaseTime);

            return;
        } else {

            if (Ncc.dhcpLogLevel >= 6) {
                logger.info("Lease not found for clientMAC='" + request.getClientMAC() + "'");
            }

            try {
                NccDhcpRelayAgentData agentData = new NccDhcpRelayAgent().getRelayAgentByIP(request.getRelayAgent());

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
                            leaseData = new NccDhcpLeases().allocateLease(uid, poolData, request);
                        } catch (NccDhcpException e) {
                            e.printStackTrace();
                        }

                        if (leaseData != null) {
                            sendReply(NccDhcpPacket.DHCP_MSG_TYPE_OFFER, leaseData, poolData.poolLeaseTime);
                            //new NccDhcpLeases().renewLease(leaseData);
                            return;
                        } else {
                            logger.error("Can't allocate lease");
                            sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
                            return;
                        }
                    } else {
                        logger.error("Pool for relayAgent='" + request.getRelayAgentName() + "' not found");
                        sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
                        return;
                    }
                } else {
                    logger.error("relayAgent='" + request.getRelayAgentName() + "' not found");
                }
            } catch (NccDhcpRelayAgentException e) {
                e.printStackTrace();
            }

            sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
            return;
        }
    }

    private void requestRequest(NccDhcpPacket pkt) {
        if (Ncc.dhcpLogLevel >= 5) logRequest(pkt);

        NccDhcpRequest request = new NccDhcpRequest(pkt);

        if (request.getRemoteID().equals("")) {
            if (Ncc.dhcpLogLevel >= 6)
                logger.info("Empty remoteID clientMAC='" + pkt.getClientID() + "'");
        } else {
            NccDhcpBindData bindData = checkBind(request);
        }

        if (!NccUtils.long2ip(request.getClientIP()).equals("0.0.0.0")) {     // renew lease

            if (Ncc.dhcpLogLevel >= 6)
                logger.info("Lease RENEW clientMAC='" + pkt.getClientID() + "'");

            NccDhcpLeaseData leaseData = new NccDhcpLeases().getLeaseByRequest(request);

            if (leaseData != null) {
                if (Ncc.dhcpLogLevel >= 6)
                    logger.info("Found lease for relayAgent='" + request.getRelayAgentName() + "' clientMAC='" + request.getClientMAC() + "'");

                NccPoolData poolData = new NccPools().getPool(leaseData.leasePool);

                if (poolData != null) {
                    new NccDhcpLeases().renewLease(leaseData);

                    sendReply(NccDhcpPacket.DHCP_MSG_TYPE_ACK, leaseData, poolData.poolLeaseTime);
                    return;

                } else {
                    logger.error("Pool " + leaseData.leasePool + " not found");
                    sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
                    return;
                }

            } else {
                logger.error("Lease for clientMAC='" + request.getClientMAC() + "' not found");
                sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
                return;
            }
        } else if (request.getRequestIP() != null) { // accept new lease

            if (Ncc.dhcpLogLevel >= 6)
                logger.info("Lease ACCEPT clientMAC='" + pkt.getClientID() + "'");

            NccDhcpLeaseData leaseData = new NccDhcpLeases().acceptLease(request);

            if (leaseData != null) {

                NccPoolData poolData = new NccPools().getPool(leaseData.leasePool);

                sendReply(NccDhcpPacket.DHCP_MSG_TYPE_ACK, leaseData, poolData.poolLeaseTime);
                return;
            } else {
                logger.error("Lease not found for clientMAC='" + request.getClientMAC() + "'");
                sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
                return;
            }
        }

    }

    private void requestRelease(NccDhcpPacket pkt) {
        if (Ncc.dhcpLogLevel >= 5) logRequest(pkt);
        NccDhcpRequest request = new NccDhcpRequest(pkt);
        NccDhcpLeaseData leaseData = new NccDhcpLeases().getLeaseByRequest(request);

        if (leaseData != null) {
            if (Ncc.dhcpLogLevel >= 5)
                logger.info("Releasing lease after DHCPRELEASE for clientMAC='" + request.getClientMAC() + "' clientIP='" + request.getClientIP() + "'");
            new NccDhcpLeases().releaseLease(leaseData);
        } else {
            logger.error("Lease not found for clientMAC='" + request.getClientMAC() + "'");
        }
    }

    private void requestDecline(NccDhcpPacket pkt) {
        if (Ncc.dhcpLogLevel >= 5) logRequest(pkt);
        NccDhcpRequest request = new NccDhcpRequest(pkt);
        NccDhcpLeaseData leaseData = new NccDhcpLeases().getLeaseByRequest(request);

        if (leaseData != null) {
            if (Ncc.dhcpLogLevel >= 5)
                logger.info("Releasing lease after DHCPDECLINE for clientMAC='" + request.getClientMAC() + "' clientIP='" + request.getClientIP() + "'");
            new NccDhcpLeases().releaseLease(leaseData);
        } else {
            logger.error("Lease not found for clientMAC='" + request.getClientMAC() + "'");
        }
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

            switch (pkt.getType()) {
                case NccDhcpPacket.DHCP_MSG_TYPE_INFORM:
                    requestInform(pkt);
                    break;
                case NccDhcpPacket.DHCP_MSG_TYPE_DISCOVER:
                    requestDiscover(pkt);
                    break;
                case NccDhcpPacket.DHCP_MSG_TYPE_REQUEST:
                    requestRequest(pkt);
                    break;
                case NccDhcpPacket.DHCP_MSG_TYPE_RELEASE:
                    requestRelease(pkt);
                    break;
                case NccDhcpPacket.DHCP_MSG_TYPE_DECLINE:
                    requestDecline(pkt);
                    break;
                default:
                    break;
            }

        } catch (NccDhcpException e) {
            e.printStackTrace();
        }
    }
}
