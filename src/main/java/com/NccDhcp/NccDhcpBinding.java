package com.NccDhcp;

import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccDhcpBinding {

    private static Logger logger = Logger.getLogger(NccDhcpServer.class);
    private NccQuery query;

    public NccDhcpBinding() {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    private NccDhcpBindData fillBindData(CachedRowSetImpl rs){
        NccDhcpBindData bindData = new NccDhcpBindData();

        try {
            bindData.id = rs.getInt("id");
            bindData.uid = rs.getInt("uid");
            bindData.remoteID = rs.getString("remoteID");
            bindData.circuitID = rs.getString("circuitID");
            bindData.clientMAC = rs.getString("clientMAC");
            bindData.relayAgent = rs.getLong("relayAgent");

            return bindData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bindData;
    }

    public NccDhcpBindData getBinding(Integer uid) {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT " +
                    "id, " +
                    "uid, " +
                    "remoteID, " +
                    "circuitID, " +
                    "clientMAC, " +
                    "relayAgent " +
                    "FROM nccDhcpBinding " +
                    "WHERE uid=" + uid);

            if(rs!=null){
                try {
                    if(rs.next()){
                        return fillBindData(rs);
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

    public NccDhcpBindData getBinding(String remoteID, String circuitID, String clientMAC, Long relayAgent) {

        CachedRowSetImpl rs;
        String whereMAC = "";

        if (clientMAC != null) {
            whereMAC = "clientMAC='" + clientMAC + "' AND ";
        }

        try {
            rs = query.selectQuery("SELECT " +
                    "id, " +
                    "uid, " +
                    "remoteID, " +
                    "circuitID, " +
                    "clientMAC, " +
                    "relayAgent " +
                    "FROM nccDhcpBinding WHERE " +
                    "remoteID='" + remoteID + "' AND " +
                    "circuitID='" + circuitID + "' AND " +
                    whereMAC +
                    "relayAgent=" + relayAgent);

            if (rs != null) {
                try {
                    if (rs.next()) {
                        return fillBindData(rs);
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

    public void setUnbinded(String remoteID, String circuitID, String clientMAC, Long relayAgent) {

        ArrayList<Integer> ids;
        Long lastSeen = System.currentTimeMillis() / 1000L;

        try {
            ids = query.updateQuery("UPDATE ncc_dhcp_unbinded SET lastSeen=" + lastSeen + ", clientMAC='" + clientMAC + "' WHERE " +
                    "remoteID='" + remoteID + "' AND " +
                    "circuitID='" + circuitID + "' AND " +
                    "relayAgent=" + relayAgent);

            if (ids == null) ids = query.updateQuery("INSERT INTO ncc_dhcp_unbinded (" +
                    "lastSeen, " +
                    "remoteID, " +
                    "circuitID, " +
                    "clientMAC, " +
                    "relayAgent) VALUES (" +
                    lastSeen + ", " +
                    "'" + remoteID + "', " +
                    "'" + circuitID + "', " +
                    "'" + clientMAC + "', " +
                    relayAgent + ")");

        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public void cleanupBinding() {
        Long cleanupTime = System.currentTimeMillis() / 1000L;

        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM ncc_dhcp_unbinded WHERE lastSeen+120<" + cleanupTime);
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

    }
}
