package com.NccDhcp;

import com.NccSystem.NccAbstractData;
import java.sql.SQLException;

public class NccDhcpLeaseData extends NccAbstractData<NccDhcpLeaseData> {
    public Integer id;
    public Long leaseStart;
    public Long leaseExpire;
    public Long leaseIP;
    public Long leaseRouter;
    public Long leaseNetmask;
    public Long leaseDNS1;
    public Long leaseDNS2;
    public Long leaseNextServer;
    public String leaseClientMAC;
    public String leaseRemoteID;
    public String leaseCircuitID;
    public Long leaseRelayAgent;
    public Integer leasePool;
    public Integer leaseUID;
    public Integer transId;

    @Override
    public NccDhcpLeaseData fillData(){
        NccDhcpLeaseData leaseData = new NccDhcpLeaseData();

        try {
            leaseData.id = rs.getInt("id");
            leaseData.leaseStart = rs.getLong("leaseStart");
            leaseData.leaseExpire = rs.getLong("leaseExpire");
            leaseData.leaseIP = rs.getLong("leaseIP");
            leaseData.leaseRouter = rs.getLong("leaseRouter");
            leaseData.leaseNetmask = rs.getLong("leaseNetmask");
            leaseData.leaseDNS1 = rs.getLong("leaseDNS1");
            leaseData.leaseDNS2 = rs.getLong("leaseDNS2");
            leaseData.leaseNextServer = rs.getLong("leaseNextServer");
            leaseData.leaseClientMAC = rs.getString("leaseClientMAC");
            leaseData.leaseRemoteID = rs.getString("leaseRemoteID");
            leaseData.leaseCircuitID = rs.getString("leaseCircuitID");
            leaseData.leaseRelayAgent = rs.getLong("leaseRelayAgent");
            leaseData.leasePool = rs.getInt("leasePool");
            leaseData.leaseUID = rs.getInt("leaseUID");
            leaseData.transId = rs.getInt("transId");

            return leaseData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return leaseData;
    }
}
