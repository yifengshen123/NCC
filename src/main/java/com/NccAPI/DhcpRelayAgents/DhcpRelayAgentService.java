package com.NccAPI.DhcpRelayAgents;

import com.NccDhcp.NccDhcpRelayAgentData;
import com.NccDhcp.NccDhcpRelayAgentType;

import java.util.ArrayList;

public interface DhcpRelayAgentService {
    public ArrayList<NccDhcpRelayAgentData> getDhcpRelayAgent(String apiKey);
    public NccDhcpRelayAgentData getDhcpRelayAgent(String apiKey, Integer id);
    public NccDhcpRelayAgentData getDhcpRelayAgentByIP(String apiKey, Long ip);

    public ArrayList<NccDhcpRelayAgentType> getDhcpRelayAgentTypes(String apiKey);

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
