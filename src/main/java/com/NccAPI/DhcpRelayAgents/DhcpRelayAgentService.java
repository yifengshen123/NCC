package com.NccAPI.DhcpRelayAgents;

import com.NccRelayAgent.NccRelayAgentData;
import com.NccRelayAgent.NccRelayAgentType;

import java.util.ArrayList;

public interface DhcpRelayAgentService {
    public ArrayList<NccRelayAgentData> getDhcpRelayAgent(String apiKey);
    public NccRelayAgentData getDhcpRelayAgent(String apiKey, Integer id);
    public NccRelayAgentData getDhcpRelayAgentByIP(String apiKey, Long ip);

    public ArrayList<NccRelayAgentType> getDhcpRelayAgentTypes(String apiKey);

    public Integer createDhcpRelayAgent(
            String apiKey,
            String agentName,
            Integer agentType,
            Long agentIP,
            Integer agentPool,
            String agentStreet,
            String agentBuild,
            Integer agentUnbindedPool,
            String agentLogin,
            String agentPassword,
            String agentEnablePassword);

    public Integer updateDhcpRelayAgent(
            String apiKey,
            Integer id,
            String agentName,
            Integer agentType,
            Long agentIP,
            Integer agentPool,
            String agentStreet,
            String agentBuild,
            Integer agentUnbindedPool,
            String agentLogin,
            String agentPassword,
            String agentEnablePassword);

    public Integer deleteDhcpRelayAgent(String apiKey, Integer id);
}
