package com.NccDhcp;

import com.NccSystem.NccAbstractData;
import java.sql.SQLException;

public class NccDhcpRelayAgentData extends NccAbstractData<NccDhcpRelayAgentData> {
    public Integer id;
    public String agentName;
    public Long agentIP;
    public Integer agentPool;
    public Integer agentType;
    public String agentStreet;
    public String agentBuild;
    public String agentLogin;
    public String agentPassword;
    public String agentEnablePassword;
    public Integer agentUnbindedPool;
    public Integer checkCircuitId;

    @Override
    public NccDhcpRelayAgentData fillData() {
        NccDhcpRelayAgentData agentData = new NccDhcpRelayAgentData();
        try {

            agentData.id = rs.getInt("id");
            agentData.agentName = rs.getString("agentName");
            agentData.agentIP = rs.getLong("agentIP");
            agentData.agentPool = rs.getInt("agentPool");
            agentData.agentType = rs.getInt("agentType");
            agentData.agentStreet = rs.getString("agentStreet");
            agentData.agentBuild = rs.getString("agentBuild");
            agentData.agentLogin = rs.getString("agentLogin");
            agentData.agentPassword = rs.getString("agentPassword");
            agentData.agentEnablePassword = rs.getString("agentEnablePassword");
            agentData.agentUnbindedPool = rs.getInt("agentUnbindedPool");
            agentData.checkCircuitId = rs.getInt("checkCircuitId");

            return agentData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return agentData;
    }
}
