package com.NccRelayAgent;

import com.NccRelayAgent.NccRelayAgentData;
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

    private NccRelayAgentData fillRelayAgentData(CachedRowSetImpl rs) {

        NccRelayAgentData relayAgentData = new NccRelayAgentData();

        try {
            relayAgentData.id = rs.getInt("id");
            relayAgentData.agentName = rs.getString("agentName");
            relayAgentData.agentIP = rs.getLong("agentIP");
            relayAgentData.agentPool = rs.getInt("agentPool");
            relayAgentData.agentType = rs.getInt("agentType");
            relayAgentData.agentStreet = rs.getString("agentStreet");
            relayAgentData.agentBuild = rs.getString("agentBuild");
            relayAgentData.agentLogin = rs.getString("agentLogin");
            relayAgentData.agentPassword = rs.getString("agentPassword");
            relayAgentData.agentEnablePassword = rs.getString("agentEnablePassword");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return relayAgentData;
    }

    public NccRelayAgent() {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<NccRelayAgentData> getRelayAgent() throws NccRelayAgentException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpRelayAgents");

            if (rs != null) {
                ArrayList<NccRelayAgentData> agents = new ArrayList<>();
                try {
                    while (rs.next()) {
                        NccRelayAgentData agentData = fillRelayAgentData(rs);
                        if (agentData != null) {
                            agents.add(agentData);
                        }
                    }
                    return agents;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccRelayAgentData getRelayAgent(Integer id) throws NccRelayAgentException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpRelayAgents WHERE id=" + id);

            if (rs != null) {
                try {
                    if (rs.next()) {
                        NccRelayAgentData agentData = fillRelayAgentData(rs);
                        if (agentData != null) {
                            return agentData;
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

    public NccRelayAgentData getRelayAgentByIP(Long ip) throws NccRelayAgentException {

        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpRelayAgents WHERE agentIP=" + ip);

            if (rs != null) {

                try {
                    if (rs.next()) {
                        return fillRelayAgentData(rs);
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
                    "agentUnbindedPool) VALUES(" +
                    "'" + relayAgentData.agentName + "', " +
                    relayAgentData.agentType + ", " +
                    relayAgentData.agentIP + ", " +
                    relayAgentData.agentPool + ", " +
                    "'" + relayAgentData.agentStreet + "', " +
                    "'" + relayAgentData.agentBuild + "', " +
                    relayAgentData.agentUnbindedPool + ")");

            if (ids != null && ids.size() > 0) {
                return ids.get(0);
            }

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }
}
