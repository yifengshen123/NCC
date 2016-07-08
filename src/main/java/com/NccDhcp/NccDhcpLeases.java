package com.NccDhcp;

import com.Ncc;
import com.NccPools.NccPoolData;
import com.NccPools.NccPools;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccDhcpLeases {
    private static Logger logger = Logger.getLogger(NccDhcpServer.class);
    private NccQuery query;

    public NccDhcpLeases() {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<NccDhcpLeaseData> getLeases() throws NccDhcpException {

        return new NccDhcpLeaseData().getDataList("SELECT * FROM nccDhcpLeases");
    }

    public NccDhcpLeaseData getLeases(Integer id) throws NccDhcpException {

        return new NccDhcpLeaseData().getData("SELECT * FROM nccDhcpLeases WHERE id=" + id);
    }

    public NccDhcpLeaseData allocateLease(Integer uid, NccPoolData poolData, NccDhcpRequest request) throws NccDhcpException {

        try {
            ArrayList<NccDhcpLeaseData> leases = getLeases();

            for (Long ip = poolData.poolStart; ip <= poolData.poolEnd; ip++) {

                Long allocated = ip;

                for (NccDhcpLeaseData lease : leases) {
                    if (lease.leaseIP.equals(ip)) {
                        allocated = 0L;
                        break;
                    } else allocated = ip;
                }

                if (allocated > 0) {
                    NccDhcpLeaseData newLease = new NccDhcpLeaseData();
                    Long leaseStart = System.currentTimeMillis() / 1000L;
                    Long leaseExpire = leaseStart + poolData.poolLeaseTime;

                    try {
                        ArrayList<Integer> id = query.updateQuery("INSERT INTO nccDhcpLeases (" +
                                "leaseStart, " +
                                "leaseExpire, " +
                                "leaseIP, " +
                                "leaseRouter, " +
                                "leaseNetmask, " +
                                "leaseDNS1, " +
                                "leaseDNS2, " +
                                "leaseNextServer, " +
                                "leaseClientMAC, " +
                                "leaseRemoteID, " +
                                "leaseCircuitID, " +
                                "leaseRelayAgent, " +
                                "leaseStatus, " +
                                "leaseUID, " +
                                "leasePool, " +
                                "transId) VALUES (" +
                                leaseStart + ", " +
                                leaseExpire + ", " +
                                allocated + ", " +
                                poolData.poolRouter + ", " +
                                poolData.poolNetmask + ", " +
                                poolData.poolDNS1 + ", " +
                                poolData.poolDNS2 + ", " +
                                poolData.poolNextServer + ", " +
                                "'" + request.getClientMAC() + "', " +
                                "'" + request.getRemoteID() + "', " +
                                "'" + request.getCircuitID() + "', " +
                                request.getRelayAgent() + ", " +
                                "0, " +
                                uid + ", " +
                                poolData.id + ", " +
                                "0" +
                                ")");

                        if (id.get(0) > 0) {
                            newLease.id = id.get(0);
                            newLease.leaseStart = leaseStart;
                            newLease.leaseExpire = leaseExpire;
                            newLease.leaseIP = allocated;
                            newLease.leaseRouter = poolData.poolRouter;
                            newLease.leaseNetmask = poolData.poolNetmask;
                            if (poolData.poolDNS1 > 0) {
                                newLease.leaseDNS1 = poolData.poolDNS1;
                            } else newLease.leaseDNS1 = null;
                            if (poolData.poolDNS2 > 0) {
                                newLease.leaseDNS2 = poolData.poolDNS2;
                            } else newLease.leaseDNS2 = null;
                            if (poolData.poolNextServer > 0) {
                                newLease.leaseNextServer = poolData.poolNextServer;
                            } else newLease.leaseNextServer = null;
                            newLease.leaseClientMAC = request.getClientMAC();
                            newLease.leaseRemoteID = request.getRemoteID();
                            newLease.leaseCircuitID = request.getCircuitID();
                            newLease.leaseRelayAgent = request.getRelayAgent();
                            newLease.leasePool = poolData.id;
                            newLease.transId = request.getTransID();

                            return newLease;
                        }

                    } catch (NccQueryException e) {
                        e.printStackTrace();
                    }
                }
            }

            throw new NccDhcpException("No free addresses in pool: " + poolData.poolName);

        } catch (NccDhcpException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccDhcpLeaseData getLeaseByUid(Integer uid) throws NccDhcpException {

        return new NccDhcpLeaseData().getData("SELECT * FROM nccDhcpLeases WHERE leaseUID=" + uid);
    }

    public NccDhcpLeaseData getLeaseByIP(Long ip) throws NccDhcpException {

        return new NccDhcpLeaseData().getData("SELECT * FROM nccDhcpLeases WHERE leaseIP=" + ip);
    }

    public NccDhcpLeaseData getLeaseByRequest(NccDhcpRequest request) {
        String relayAgentWhere = "";
        String circuitIDWhere = "";

        if (request.getRelayAgent() > 0) {
            relayAgentWhere = " AND leaseRelayAgent=" + request.getRelayAgent();
        }

        if (!request.getCircuitID().equals("")) {
            relayAgentWhere = " AND leaseCircuitID='" + request.getCircuitID() + "'";
        }

        return new NccDhcpLeaseData().getData("SELECT * FROM nccDhcpLeases WHERE leaseClientMAC='" + request.getClientMAC() + "'" + relayAgentWhere + circuitIDWhere);
    }

    public NccDhcpLeaseData acceptLease(NccDhcpRequest request) {

        CachedRowSetImpl rs;

        try {
            String condition = "leaseClientMAC='" + request.getClientMAC() + "' ";

            if (!request.getRemoteID().equals("")) condition += "AND leaseRemoteID='" + request.getRemoteID() + "' ";
            if (!request.getCircuitID().equals("")) condition += "AND leaseCircuitID='" + request.getCircuitID() + "' ";

            rs = query.selectQuery("SELECT id FROM nccDhcpLeases WHERE " +
                    "leaseIP=" + request.getClientIP() + " AND " +
                    condition);

            if (rs != null) {
                try {
                    if (rs.next()) {
                        try {
                            Integer id = rs.getInt("id");
                            NccDhcpLeaseData lease = getLeases(id);

                            if (lease != null) {
                                NccPoolData poolData = new NccPools().getPool(lease.leasePool);

                                Long leaseStart = System.currentTimeMillis() / 1000L;
                                Long leaseExpire = leaseStart + poolData.poolLeaseTime;
                                Integer interim = poolData.poolLeaseTime + Math.round(poolData.poolLeaseTime / 3);

                                query.updateQuery("UPDATE nccDhcpLeases SET leaseStatus=1, leaseStart=UNIX_TIMESTAMP(NOW()), leaseExpire=UNIX_TIMESTAMP(NOW())+" + interim + " WHERE id=" + id);
                                return lease;
                            }
                            return null;
                        } catch (NccDhcpException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void renewLease(NccDhcpLeaseData leaseData) {

        NccPoolData poolData = new NccPools().getPool(leaseData.leasePool);
        Integer interim = poolData.poolLeaseTime + Math.round(poolData.poolLeaseTime / 3);

        try {
            query.updateQuery("UPDATE nccDhcpLeases SET " +
                    "leaseStatus=1, " +
                    "leaseExpire=UNIX_TIMESTAMP(NOW())+" + interim + " " +
                    "WHERE " +
                    "leaseRelayAgent=" + leaseData.leaseRelayAgent + " AND " +
                    "leaseClientMAC='" + leaseData.leaseClientMAC + "'");
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public void releaseLease() {

    }

    public void cleanupLeases() {
        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM nccDhcpLeases WHERE leaseExpire<UNIX_TIMESTAMP(NOW())");
            if (ids != null) for (Integer id : ids) {
                if (Ncc.dhcpLogLevel >= 6) logger.debug("Lease expired: " + id);
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }
}
