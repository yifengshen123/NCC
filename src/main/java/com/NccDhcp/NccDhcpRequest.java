package com.NccDhcp;

import com.NccSystem.NccUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NccDhcpRequest {
    public String clientMAC;
    public String remoteID;
    public String circuitID;
    public Long relayAgent;
    public Long clientIP;
    public Long requestIP;
    public Integer transID;
    private NccDhcpPacket pkt;

    public NccDhcpRequest(String remoteID, String circuitID, String clientMAC, Long relayAgent) {

        setClientMAC(clientMAC);
        setRemoteID(remoteID);
        setCircuitID(circuitID);
        try {
            setRelayAgent(InetAddress.getByName(NccUtils.long2ip(relayAgent)));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public NccDhcpRequest(NccDhcpPacket pkt) {
        this.pkt = pkt;

        setClientMAC(pkt.getClientMAC());
        setRemoteID(pkt.getOpt82RemoteID());
        setCircuitID(pkt.getOpt82CircuitID());
        setRelayAgent(pkt.getRelayAgent());
        setTransID(pkt.ba2int(pkt.dhcpTransID));

        if (pkt.getClientIPAddress() != null) {
            setClientIP(NccUtils.ip2long(pkt.getClientIPAddress().getHostAddress()));
        }

        if (pkt.getAddressRequest() != null) {
            setRequestIP(NccUtils.ip2long(pkt.getAddressRequest().getHostAddress()));
        }
    }

    public void setClientMAC(String clientMAC) {
        this.clientMAC = clientMAC;
    }

    public void setRemoteID(String remoteID) {
        this.remoteID = remoteID;
    }

    public void setCircuitID(String circuitID) {
        this.circuitID = circuitID;
    }

    public void setRelayAgent(InetAddress relayAgent) {
        this.relayAgent = NccUtils.ip2long(relayAgent.getHostAddress());
    }

    public void setTransID(Integer transID) {
        this.transID = transID;
    }

    public void setClientIP(Long clientIP) {
        this.clientIP = clientIP;
    }

    public void setRequestIP(Long requestIP) {
        this.requestIP = requestIP;
    }

    public String getClientMAC() {
        return this.clientMAC;
    }

    public String getRemoteID() {
        return this.remoteID;
    }

    public String getCircuitID() {
        return this.circuitID;
    }

    public Long getRelayAgent() {
        return this.relayAgent;
    }

    public String getRelayAgentName() {
        return NccUtils.long2ip(this.getRelayAgent());
    }

    public Integer getTransID() {
        return this.transID;
    }

    public Long getClientIP() {
        return this.clientIP;
    }

    public Long getRequestIP() {
        return this.requestIP;
    }
}
