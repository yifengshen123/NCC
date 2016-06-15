package com.NccAPI.RelayAgents;

import com.NccRelayAgent.NccRelayAgentData;
import com.NccRelayAgent.NccRelayAgentType;

import java.util.ArrayList;

public interface RelayAgentService {
    ArrayList<NccRelayAgentData> getRelayAgent(String apiKey);
    NccRelayAgentData getRelayAgent(String apiKey, Integer id);

    ArrayList<NccRelayAgentType> getRelayAgentTypes(String apiKey);

    Integer createRelayAgent(String apiKey, String agentName, Integer agentType, Long agentIP, Integer agentPool, String agentStreet, String agentBuild, Integer agentUnbindedPool);
}
