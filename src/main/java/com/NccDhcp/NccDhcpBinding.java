package com.NccDhcp;

import com.Ncc;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.log4j.Logger;

import java.sql.Array;
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

    private NccDhcpBindData fillBindData(CachedRowSetImpl rs) {
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

    private NccDhcpUnbindedData fillUnbindedData(CachedRowSetImpl rs) {
        NccDhcpUnbindedData unbindedData = new NccDhcpUnbindedData();

        try {
            unbindedData.id = rs.getInt("id");
            unbindedData.remoteID = rs.getString("remoteID");
            unbindedData.circuitID = rs.getString("circuitID");
            unbindedData.clientMAC = rs.getString("clientMAC");
            unbindedData.relayAgent = rs.getLong("relayAgent");

            return unbindedData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unbindedData;
    }

    public NccDhcpBindData getBinding(Integer uid) {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpBinding WHERE uid=" + uid);

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

    public ArrayList<NccDhcpBindData> getBinding() {
        CachedRowSetImpl rs;
        ArrayList<NccDhcpBindData> bindings = new ArrayList<>();

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpBinding");

            if (rs != null) {
                try {
                    while (rs.next()) {
                        NccDhcpBindData bindData = fillBindData(rs);
                        if (bindData != null) {
                            bindings.add(bindData);
                        }
                    }

                    return bindings;
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
            rs = query.selectQuery("SELECT * FROM nccDhcpBinding WHERE " +
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

    public NccDhcpUnbindedData getUnbinded(Integer id) {

        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpUnbinded WHERE id=" + id);

            if (rs != null) {
                try {
                    if (rs.next()) {
                        return fillUnbindedData(rs);
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

    public ArrayList<NccDhcpUnbindedData> getUnbinded() {

        CachedRowSetImpl rs;
        ArrayList<NccDhcpUnbindedData> unbinded = new ArrayList<>();

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpUnbinded");

            if (rs != null) {
                try {
                    while (rs.next()) {
                        NccDhcpUnbindedData unbindedData = fillUnbindedData(rs);

                        if (unbindedData != null) {
                            unbinded.add(unbindedData);
                        }
                    }

                    return unbinded;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void clearBinding(Integer uid) {
        ArrayList<Integer> ids;

        try {
            ids = query.updateQuery("DELETE FROM nccDhcpBinding WHERE uid=" + uid);
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public void setUnbinded(String remoteID, String circuitID, String clientMAC, Long relayAgent) {

        ArrayList<Integer> ids;
        Long lastSeen = System.currentTimeMillis() / 1000L;

        try {
            ids = query.updateQuery("UPDATE nccDhcpUnbinded SET lastSeen=" + lastSeen + ", clientMAC='" + clientMAC + "' WHERE " +
                    "remoteID='" + remoteID + "' AND " +
                    "circuitID='" + circuitID + "' AND " +
                    "relayAgent=" + relayAgent);

            if (ids == null) ids = query.updateQuery("INSERT INTO nccDhcpUnbinded (" +
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

    public Integer setBinding(Integer uid, String remoteID, String circuitID, String clientMAC, Long relayAgent) {

        ArrayList<Integer> ids;

        try {
            ids = query.updateQuery("INSERT INTO nccDhcpBinding (" +
                    "uid, " +
                    "remoteID, " +
                    "circuitID, " +
                    "clientMAC, " +
                    "relayAgent" +
                    ") VALUES (" +
                    uid + ", " +
                    "'" + remoteID + "', " +
                    "'" + circuitID + "', " +
                    "'" + clientMAC + "', " +
                    relayAgent + ")");

            return ids.get(0);

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void cleanupBinding() {

        Integer cleanupTime = Ncc.dhcpUnbindedCleanupTime * 60;

        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM nccDhcpUnbinded WHERE lastSeen+" + cleanupTime + "<UNIX_TIMESTAMP(NOW())");
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

    }
}
