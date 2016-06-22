package com.NccDhcp;

import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccDhcpLeaseData {
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

    private CachedRowSetImpl rs;

    NccDhcpLeaseData() {
    }

    NccDhcpLeaseData(CachedRowSetImpl rs) throws NccDhcpException {
        this.rs = rs;
        if (rs == null) {
            throw new NccDhcpException("rs is null");
        }
    }

    private NccDhcpLeaseData fillData() throws NccDhcpException {
        try {
            this.id = this.rs.getInt("id");
            this.leaseStart = this.rs.getLong("leaseStart");
            this.leaseExpire = this.rs.getLong("leaseExpire");
            this.leaseIP = this.rs.getLong("leaseIP");
            this.leaseRouter = this.rs.getLong("leaseRouter");
            this.leaseNetmask = this.rs.getLong("leaseNetmask");
            this.leaseDNS1 = this.rs.getLong("leaseDNS1");
            this.leaseDNS2 = this.rs.getLong("leaseDNS2");
            this.leaseNextServer = this.rs.getLong("leaseNextServer");
            this.leaseClientMAC = this.rs.getString("leaseClientMAC");
            this.leaseRemoteID = this.rs.getString("leaseRemoteID");
            this.leaseCircuitID = this.rs.getString("leaseCircuitID");
            this.leaseRelayAgent = this.rs.getLong("leaseRelayAgent");
            this.leasePool = this.rs.getInt("leasePool");
            this.leaseUID = this.rs.getInt("leaseUID");
            this.transId = this.rs.getInt("transId");
            return this;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NccDhcpException("rs.get error");
        }
    }

    NccDhcpLeaseData getData() throws NccDhcpException {
        try {
            if (this.rs.next()) {
                return fillData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    ArrayList<NccDhcpLeaseData> getDataList() throws NccDhcpException {
        ArrayList<NccDhcpLeaseData> leaseDatas = new ArrayList<>();

        try {
            while (this.rs.next()) {
                leaseDatas.add(fillData());
            }
            return leaseDatas;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
