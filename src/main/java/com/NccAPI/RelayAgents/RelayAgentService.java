package com.NccAPI.RelayAgents;

import com.NccRelayAgent.NccRelayAgentData;
import com.NccRelayAgent.NccRelayAgentType;

import java.util.ArrayList;

public interface RelayAgentService {
    public ArrayList<NccRelayAgentData> getRelayAgent(String apiKey);
    public NccRelayAgentData getRelayAgent(String apiKey, Integer id);

    public ArrayList<NccRelayAgentType> getRelayAgentTypes(String apiKey);

    public Integer createRelayAgent(String apiKey, String agentName, Integer agentType, Long agentIP, Integer agentPool, String agentStreet, String agentBuild, Integer agentUnbindedPool);
}
