package com.NccRelayAgent;

import com.NccSystem.NccAbstractData;
import java.sql.SQLException;

public class NccRelayAgentData extends NccAbstractData<NccRelayAgentData> {
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

    @Override
    public NccRelayAgentData fillData() {
        NccRelayAgentData agentData = new NccRelayAgentData();
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

            return agentData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return agentData;
    }
}
