package com.NccRelayAgent;

import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccRelayAgent {

    private NccQuery query;

    private NccRelayAgentType fillRelayAgentType(CachedRowSetImpl rs) {
        NccRelayAgentType relayAgentType = new NccRelayAgentType();

        try {
            relayAgentType.id = rs.getInt("id");
            relayAgentType.typeName = rs.getString("typeName");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return relayAgentType;
    }

    public NccRelayAgent() {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<NccRelayAgentData> getRelayAgent() throws NccRelayAgentException {

        return new NccRelayAgentData().getDataList("SELECT * FROM nccDhcpRelayAgents");
    }

    public NccRelayAgentData getRelayAgent(Integer id) throws NccRelayAgentException {

        return new NccRelayAgentData().getData("SELECT * FROM nccDhcpRelayAgents WHERE id=" + id);
    }

    public NccRelayAgentData getRelayAgentByIP(Long ip) throws NccRelayAgentException {

        return new NccRelayAgentData().getData("SELECT * FROM nccDhcpRelayAgents WHERE agentIP=" + ip);
    }

    public ArrayList<NccRelayAgentType> getRelayAgentTypes() throws NccRelayAgentException {

        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpRelayAgentTypes");

            if (rs != null) {
                ArrayList<NccRelayAgentType> types = new ArrayList<>();
                try {
                    while (rs.next()) {
                        NccRelayAgentType relayAgentType = fillRelayAgentType(rs);

                        if (relayAgentType != null) {
                            types.add(relayAgentType);
                        }
                    }
                    return types;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Integer createRelayAgent(NccRelayAgentData relayAgentData) throws NccRelayAgentException {

        try {
            ArrayList<Integer> ids = query.updateQuery("INSERT INTO nccDhcpRelayAgents (" +
                    "agentName, " +
                    "agentType, " +
                    "agentIP, " +
                    "agentPool, " +
                    "agentStreet, " +
                    "agentBuild, " +
                    "agentLogin, " +
                    "agentPassword, " +
                    "agentEnablePassword, " +
                    "agentUnbindedPool) VALUES(" +
                    "'" + relayAgentData.agentName + "', " +
                    relayAgentData.agentType + ", " +
                    relayAgentData.agentIP + ", " +
                    relayAgentData.agentPool + ", " +
                    "'" + relayAgentData.agentStreet + "', " +
                    "'" + relayAgentData.agentBuild + "', " +
                    "'" + relayAgentData.agentLogin + "', " +
                    "'" + relayAgentData.agentPassword + "', " +
                    "'" + relayAgentData.agentEnablePassword + "', " +
                    relayAgentData.agentUnbindedPool + ")");

            if (ids != null && ids.size() > 0) {
                return ids.get(0);
            }

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Integer updateRelayAgent(NccRelayAgentData relayAgentData) throws NccRelayAgentException {

        try {
            ArrayList<Integer> ids = query.updateQuery("UPDATE nccDhcpRelayAgents SET " +
                    "agentName='" + relayAgentData.agentName + "', " +
                    "agentType=" + relayAgentData.agentType + ", " +
                    "agentIP=" + relayAgentData.agentIP + ", " +
                    "agentPool=" + relayAgentData.agentPool + ", " +
                    "agentStreet='" + relayAgentData.agentStreet + "', " +
                    "agentBuild='" + relayAgentData.agentBuild + "', " +
                    "agentLogin='" + relayAgentData.agentLogin + "', " +
                    "agentPassword='" + relayAgentData.agentPassword + "', " +
                    "agentEnablePassword='" + relayAgentData.agentEnablePassword + "', " +
                    "agentUnbindedPool=" + relayAgentData.agentUnbindedPool + " " +
                    "WHERE id=" + relayAgentData.id
            );

            if (ids != null && ids.size() > 0) {
                return ids.get(0);
            }

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Integer deleteRelayAgent(Integer id) throws NccRelayAgentException {
        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM nccDhcpRelayAgents WHERE id=" + id);

            if (ids != null && ids.size() > 0) {
                return ids.get(0);
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }
}
