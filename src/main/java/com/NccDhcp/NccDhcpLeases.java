package com.NccDhcp;

import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.log4j.Logger;

import java.net.InetAddress;
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

    private ArrayList<NccDhcpLeaseData> fillLeaseData(CachedRowSetImpl rs) {
        if (rs != null) {
            try {
                ArrayList<NccDhcpLeaseData> leases = new ArrayList<>();

                while (rs.next()) {
                    NccDhcpLeaseData leaseData = new NccDhcpLeaseData();

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

                    leases.add(leaseData);
                }

                return leases;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private NccDhcpLeaseData fillSingleLeaseData(CachedRowSetImpl rs) {
        if (rs != null) {
            try {
                if (rs.next()) {
                    NccDhcpLeaseData leaseData = new NccDhcpLeaseData();

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
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public ArrayList<NccDhcpLeaseData> getLeases() throws NccDhcpException {
        return getLeases(null);
    }

    public ArrayList<NccDhcpLeaseData> getLeases(Integer id) throws NccDhcpException {
        String whereId = "";
        CachedRowSetImpl rs;

        if (id != null) {
            whereId = " WHERE id=" + id;
        }

        try {
            rs = query.selectQuery("SELECT id, " +
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
                    "leaseUID, " +
                    "leasePool," +
                    "transId FROM ncc_dhcp_leases" + whereId);
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccDhcpException("SQL error: " + e.getMessage());
        }


        return fillLeaseData(rs);
    }

    public NccDhcpLeaseData allocateLease(Integer uid, NccDhcpPoolData poolData, String clientMAC, String remoteID, String circuitID, Long RelayAgent, Integer transId) throws NccDhcpException {

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
                        ArrayList<Integer> id = query.updateQuery("INSERT INTO ncc_dhcp_leases (" +
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
                                "'" + clientMAC + "', " +
                                "'" + remoteID + "', " +
                                "'" + circuitID + "', " +
                                RelayAgent + ", " +
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
                            newLease.leaseClientMAC = clientMAC;
                            newLease.leaseRemoteID = remoteID;
                            newLease.leaseCircuitID = circuitID;
                            newLease.leaseRelayAgent = RelayAgent;
                            newLease.leasePool = poolData.id;
                            newLease.transId = transId;

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

    public ArrayList<NccDhcpLeaseData> getLeaseByUid(Integer uid) throws NccDhcpException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT id, " +
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
                    "leaseUID, " +
                    "leasePool," +
                    "transId FROM ncc_dhcp_leases " +
                    "WHERE uid=" + uid);
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccDhcpException("SQL error: " + e.getMessage());
        }

        return fillLeaseData(rs);
    }

    public NccDhcpLeaseData getLeaseByIP(Long ip) throws NccDhcpException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT id, " +
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
                    "leaseUID, " +
                    "leasePool," +
                    "transId FROM ncc_dhcp_leases " +
                    "WHERE leaseIP=" + ip);
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccDhcpException("SQL error: " + e.getMessage());
        }

        return fillSingleLeaseData(rs);
    }

    public NccDhcpLeaseData getLeaseByMAC(Long relayAgent, String mac, Integer transId) {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT id, " +
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
                    "leaseUID, " +
                    "leasePool, " +
                    "transId FROM ncc_dhcp_leases " +
                    "WHERE leaseClientMAC='" + mac + "' AND " +
                    "leaseRelayAgent=" + relayAgent);

            if (rs.size() > 0) {
                return fillLeaseData(rs).get(0);
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccDhcpLeaseData acceptLease(Long clientIP, String clientMAC, String remoteID, String circuitID, Integer transId) {

        CachedRowSetImpl rs;

        // TODO: 4/20/16 not needed to determine id of lease
        try {
            logger.debug("SELECT id FROM ncc_dhcp_leases WHERE " +
                    "leaseIP=" + clientIP + " AND " +
                    "leaseClientMAC='" + clientMAC + "' AND " +
                    "leaseRemoteID='" + remoteID + "' AND " +
                    "leaseCircuitID='" + circuitID + "'");

            rs = query.selectQuery("SELECT id FROM ncc_dhcp_leases WHERE " +
                    "leaseIP=" + clientIP + " AND " +
                    "leaseClientMAC='" + clientMAC + "' AND " +
                    "leaseRemoteID='" + remoteID + "' AND " +
                    "leaseCircuitID='" + circuitID + "'");

            if (rs != null) {
                try {
                    if (rs.next()) {
                        try {
                            Integer id = rs.getInt("id");
                            ArrayList<NccDhcpLeaseData> leases = getLeases(id);

                            if (leases != null) {
                                NccDhcpLeaseData lease = leases.get(0);

                                if (lease != null) {
                                    NccDhcpPoolData poolData = new NccDhcpPools().getPool(lease.leasePool);

                                    Long leaseStart = System.currentTimeMillis() / 1000L;
                                    Long leaseExpire = leaseStart + poolData.poolLeaseTime;
                                    Integer interim = poolData.poolLeaseTime + Math.round(poolData.poolLeaseTime / 3);

                                    query.updateQuery("UPDATE ncc_dhcp_leases SET leaseStatus=1, leaseStart=UNIX_TIMESTAMP(NOW()), leaseExpire=UNIX_TIMESTAMP(NOW())+" + interim + " WHERE id=" + id);
                                    return lease;
                                }
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

        NccDhcpPoolData poolData = new NccDhcpPools().getPool(leaseData.leasePool);
        Integer interim = poolData.poolLeaseTime + Math.round(poolData.poolLeaseTime / 3);

        try {
            query.updateQuery("UPDATE ncc_dhcp_leases SET leaseStatus=1, leaseExpire=UNIX_TIMESTAMP(NOW())+" + interim + " WHERE leaseRelayAgent=" + leaseData.leaseRelayAgent + " AND leaseClientMAC='" + leaseData.leaseClientMAC + "'");
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public void releaseLease() {

    }

    public void cleanupLeases() {
        Long cleanupTime = System.currentTimeMillis() / 1000L;

        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM ncc_dhcp_leases WHERE leaseExpire<UNIX_TIMESTAMP(NOW())");
            if (ids != null) for (Integer id : ids) {
                logger.debug("Lease expired: " + id);
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }
}
