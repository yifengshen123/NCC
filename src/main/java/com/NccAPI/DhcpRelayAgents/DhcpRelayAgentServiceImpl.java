package com.NccAPI.DhcpRelayAgents;

import com.NccAPI.NccAPI;
import com.NccDhcp.NccDhcpRelayAgent;
import com.NccDhcp.NccDhcpRelayAgentData;
import com.NccDhcp.NccDhcpRelayAgentException;
import com.NccDhcp.NccDhcpRelayAgentType;

import java.util.ArrayList;

public class DhcpRelayAgentServiceImpl implements DhcpRelayAgentService {
    public ArrayList<NccDhcpRelayAgentData> getDhcpRelayAgent(String apiKey) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        try {
            ArrayList<NccDhcpRelayAgentData> agents = new NccDhcpRelayAgent().getRelayAgent();

            return agents;
        } catch (NccDhcpRelayAgentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccDhcpRelayAgentData getDhcpRelayAgent(String apiKey, Integer id) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        try {
            NccDhcpRelayAgentData relayAgentData = new NccDhcpRelayAgent().getRelayAgent(id);

            return relayAgentData;
        } catch (NccDhcpRelayAgentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccDhcpRelayAgentData getDhcpRelayAgentByIP(String apiKey, Long ip) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        try {
            NccDhcpRelayAgentData relayAgentData = new NccDhcpRelayAgent().getRelayAgentByIP(ip);
            return relayAgentData;
        } catch (NccDhcpRelayAgentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<NccDhcpRelayAgentType> getDhcpRelayAgentTypes(String apiKey) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        try {
            ArrayList<NccDhcpRelayAgentType> types = new NccDhcpRelayAgent().getRelayAgentTypes();
            return types;
        } catch (NccDhcpRelayAgentException e) {
            e.printStackTrace();
        }


        return null;
    }

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
            String agentEnablePassword) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        NccDhcpRelayAgentData relayAgentData = new NccDhcpRelayAgentData();

        relayAgentData.agentName = agentName;
        relayAgentData.agentType = agentType;
        relayAgentData.agentIP = agentIP;
        relayAgentData.agentPool = agentPool;
        relayAgentData.agentStreet = agentStreet;
        relayAgentData.agentBuild = agentBuild;
        relayAgentData.agentLogin = agentLogin;
        relayAgentData.agentPassword = agentPassword;
        relayAgentData.agentEnablePassword = agentEnablePassword;
        relayAgentData.agentUnbindedPool = agentUnbindedPool;

        try {
            return new NccDhcpRelayAgent().createRelayAgent(relayAgentData);
        } catch (NccDhcpRelayAgentException e) {
            e.printStackTrace();
        }

        return null;
    }

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
            String agentEnablePassword) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        NccDhcpRelayAgentData relayAgentData = new NccDhcpRelayAgentData();

        relayAgentData.id = id;
        relayAgentData.agentName = agentName;
        relayAgentData.agentType = agentType;
        relayAgentData.agentIP = agentIP;
        relayAgentData.agentPool = agentPool;
        relayAgentData.agentStreet = agentStreet;
        relayAgentData.agentBuild = agentBuild;
        relayAgentData.agentLogin = agentLogin;
        relayAgentData.agentPassword = agentPassword;
        relayAgentData.agentEnablePassword = agentEnablePassword;
        relayAgentData.agentUnbindedPool = agentUnbindedPool;

        try {
            return new NccDhcpRelayAgent().updateRelayAgent(relayAgentData);
        } catch (NccDhcpRelayAgentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Integer deleteDhcpRelayAgent(String apiKey, Integer id){
        try {
            return new NccDhcpRelayAgent().deleteRelayAgent(id);
        } catch (NccDhcpRelayAgentException e) {
            e.printStackTrace();
        }

        return null;
    }
}
