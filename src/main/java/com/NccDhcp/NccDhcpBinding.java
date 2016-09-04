package com.NccDhcp;

import com.Ncc;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import org.apache.log4j.Logger;

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

    public NccDhcpBindData getBinding(Integer uid) {

        return new NccDhcpBindData().getData("SELECT * FROM nccDhcpBinding WHERE uid=" + uid);
    }

    public ArrayList<NccDhcpBindData> getBinding() {

        return new NccDhcpBindData().getDataList("SELECT * FROM nccDhcpBinding");
    }

    public NccDhcpBindData getBinding(NccDhcpRequest request) {

        String checkMAC = "";

        if (request.getClientMAC() != null) {
            checkMAC = "clientMAC='" + request.getClientMAC() + "' AND ";
        }

        String checkCircuitId = "";

        try {
            NccDhcpRelayAgentData relayAgent = new NccDhcpRelayAgent().getRelayAgentByIP(request.getRelayAgent());

            if (relayAgent.checkCircuitId > 0) {
                checkCircuitId = "circuitID='" + request.getCircuitID() + "' AND ";
            }
        } catch (NccDhcpRelayAgentException e) {
            e.printStackTrace();
        }

        return new NccDhcpBindData().getData("SELECT * FROM nccDhcpBinding WHERE " +
                "remoteID='" + request.getRemoteID() + "' AND " +
                checkCircuitId +
                checkMAC +
                "relayAgent=" + request.getRelayAgent());
    }

    public NccDhcpUnbindedData getUnbinded(Integer id) {

        return new NccDhcpUnbindedData().getData("SELECT * FROM nccDhcpUnbinded WHERE id=" + id);
    }

    public ArrayList<NccDhcpUnbindedData> getUnbinded() {

        return new NccDhcpUnbindedData().getDataList("SELECT * FROM nccDhcpUnbinded");
    }

    public void clearBinding(Integer uid) {
        ArrayList<Integer> ids;

        try {
            ids = query.updateQuery("DELETE FROM nccDhcpBinding WHERE uid=" + uid);
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public void clearUnbinded(Integer id) {
        ArrayList<Integer> ids;

        try {
            ids = query.updateQuery("DELETE FROM nccDhcpUnbinded WHERE id=" + id);
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public void setUnbinded(NccDhcpRequest request) {

        ArrayList<Integer> ids;
        Long lastSeen = System.currentTimeMillis() / 1000L;

        try {
            ids = query.updateQuery("UPDATE nccDhcpUnbinded SET lastSeen=" + lastSeen + ", clientMAC='" + request.getClientMAC() + "' WHERE " +
                    "remoteID='" + request.getRemoteID() + "' AND " +
                    "circuitID='" + request.getCircuitID() + "' AND " +
                    "relayAgent=" + request.getRelayAgent());

            if (ids == null) ids = query.updateQuery("INSERT INTO nccDhcpUnbinded (" +
                    "lastSeen, " +
                    "remoteID, " +
                    "circuitID, " +
                    "clientMAC, " +
                    "relayAgent) VALUES (" +
                    lastSeen + ", " +
                    "'" + request.getRemoteID() + "', " +
                    "'" + request.getCircuitID() + "', " +
                    "'" + request.getClientMAC() + "', " +
                    request.getRelayAgent() + ")");

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
