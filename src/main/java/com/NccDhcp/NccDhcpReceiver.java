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
                logger.info("Unbinded user: remoteID=" + request.getRemoteID() + " circuitID=" + request.getCircuitID() + " clientMAC=" + request.getClientMAC() + " relayAgent=" + request.getRelayAgentName());

            try {
                NccDhcpRelayAgentData agent = new NccDhcpRelayAgent().getRelayAgentByIP(request.getRelayAgent());

                if (agent == null) {
                    if (Ncc.dhcpLogLevel >= 5) {
                        logger.info("Request from unknown RelayAgent: " + request.getRelayAgentName());
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

    private void requestInform(NccDhcpPacket pkt) {
        if (Ncc.dhcpLogLevel >= 5)
            logger.info("DHCPINFORM from " + inPkt.getAddress().getHostAddress() + " clientMAC: " + pkt.getClientMAC());
        if (Ncc.dhcpLogLevel >= 5)
            logger.info("RelayAgent: " + pkt.getRelayAgent().getHostAddress() + " remoteID: " + pkt.getOpt82RemoteID() + " circuitID: " + pkt.getOpt82CircuitID() + " clientID: " + pkt.getClientID());
    }

    private void requestDiscover(NccDhcpPacket pkt) {

        if (Ncc.dhcpLogLevel >= 5)
            logger.info("DHCPDISCOVER from " + inPkt.getAddress().getHostAddress() + " clientMAC: " + pkt.getClientMAC());

        if (Ncc.dhcpLogLevel >= 5)
            logger.info("RelayAgent: " + pkt.getRelayAgent().getHostAddress() + " remoteID: " + pkt.getOpt82RemoteID() + " circuitID: " + pkt.getOpt82CircuitID() + " clientMAC: " + pkt.getClientMAC());

        NccDhcpRequest request = new NccDhcpRequest(pkt);

        if (request.getRemoteID().equals("")) {
            if (Ncc.dhcpLogLevel >= 6)
                logger.info("Empty remoteID in DHCPDISCOVER clientMAC: " + pkt.getClientMAC());

            sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
            return;
        }

        NccDhcpBindData bindData = checkBind(request);

        NccDhcpLeaseData leaseData = new NccDhcpLeases().getLeaseByRequest(request);

        if (leaseData != null) {

            if (Ncc.dhcpLogLevel >= 6)
                logger.info("Found lease for " + request.getRelayAgentName() + " clientMAC=" + request.getClientMAC());

            NccPoolData poolData = new NccPools().getPool(leaseData.leasePool);

            sendReply(NccDhcpPacket.DHCP_MSG_TYPE_OFFER, leaseData, poolData.poolLeaseTime);

            return;
        } else {

            if (Ncc.dhcpLogLevel >= 6) {
                logger.info("Lease not found for clientMAC=" + request.getClientMAC());
            }

            try {
                NccDhcpRelayAgentData agentData = null;

                agentData = new NccDhcpRelayAgent().getRelayAgentByIP(request.getRelayAgent());

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
                            new NccDhcpLeases().renewLease(leaseData);
                            return;
                        } else {
                            logger.error("Can't allocate lease");
                            sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
                            return;
                        }
                    } else {
                        logger.error("Pool for relay agent " + request.getRelayAgentName() + " not found");
                        sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
                        return;
                    }
                } else {
                    logger.error("Relay agent " + request.getRelayAgentName() + " not found");
                }
            } catch (NccDhcpRelayAgentException e) {
                e.printStackTrace();
            }

            sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
            return;
        }
    }

    private void requestRequest(NccDhcpPacket pkt) {
        if (Ncc.dhcpLogLevel >= 5)
            logger.info("DHCPREQUEST from " + inPkt.getAddress().getHostAddress() + " clientMAC: " + pkt.getClientMAC());

        if (Ncc.dhcpLogLevel >= 5)
            logger.info("RelayAgent: " + pkt.getRelayAgent().getHostAddress() + " remoteID: " + pkt.getOpt82RemoteID() + " circuitID: " + pkt.getOpt82CircuitID() + " clientID: " + pkt.getClientID());

        NccDhcpRequest request = new NccDhcpRequest(pkt);

        if (request.getRemoteID().equals("")) {
            if (Ncc.dhcpLogLevel >= 6)
                logger.info("Empty remoteID clientMAC: " + pkt.getClientID());
        }

        NccDhcpLeaseData leaseData = null;

        if (!NccUtils.long2ip(request.getClientIP()).equals("0.0.0.0")) {     // renew lease

            if (Ncc.dhcpLogLevel >= 6)
                logger.info("Lease RENEW clientMAC: " + pkt.getClientID());

            leaseData = new NccDhcpLeases().getLeaseByRequest(request);

            if (leaseData != null) {
                if (Ncc.dhcpLogLevel >= 6)
                    logger.info("Found lease for " + request.getRelayAgentName() + " clientMAC=" + request.getClientMAC());

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
                logger.error("Lease for " + request.getClientMAC() + " not found");
                sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
                return;
            }
        } else if (request.getRequestIP() != null) { // accept new lease

            if (Ncc.dhcpLogLevel >= 6)
                logger.info("Lease ACCEPT clientMAC: " + pkt.getClientID());

            NccDhcpBindData bindData = checkBind(request);

            leaseData = new NccDhcpLeases().acceptLease(request);

            if (leaseData != null) {

                NccPoolData poolData = new NccPools().getPool(leaseData.leasePool);

                sendReply(NccDhcpPacket.DHCP_MSG_TYPE_ACK, leaseData, poolData.poolLeaseTime);
                return;
            } else {
                logger.error("Lease not found for " + request.getClientMAC());
                sendReply(NccDhcpPacket.DHCP_MSG_TYPE_NAK, null, 0);
                return;
            }
        }

    }

    private void requestRelease(NccDhcpPacket pkt) {
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
