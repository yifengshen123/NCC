package com.NccAPI.IptvManager;

import com.NccAPI.NccAPI;
import com.NccIptvManager.*;

import java.util.ArrayList;

public class IptvManagerImpl implements IptvManagerService {

    public Integer runIptvTransponder(String apiKey, Integer id) {
        final NccIptvManager iptvManager = new NccIptvManager();
        final Integer transponderId = id;

        if (!new NccAPI().checkPermission(apiKey, "permRunIptvTransponder")) return null;

        iptvManager.stopTransponder(id);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                iptvManager.runTransponder(transponderId);
            }
        });
        t.start();

        return 0;
    }

    public ArrayList<Integer> stopIptvTransponder(String apiKey, Integer id) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permStopIptvTransponder")) return null;

        return iptvManager.stopTransponder(id);
    }

    public TransponderStatus getIptvTransponderStatus(String apiKey, Integer id) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permIptvGetTransponderStatus")) return null;

        return iptvManager.getTransponderStatus(id);
    }

    public ArrayList<ServerData> getIptvServers(String apiKey) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permGetIptvServers")) return null;

        return iptvManager.getServers();
    }

    public ArrayList<AdapterData> getIptvAdapters(String apiKey) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permGetIptvAdapters")) return null;

        return iptvManager.getAdapters();
    }

    public ArrayList<AdapterData> getIptvAdaptersByServerId(String apiKey, Integer id) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permGetIptvAdaptersByServerId")) return null;

        return iptvManager.getAdaptersByServerId(id);
    }

    public ArrayList<AdapterType> getIptvAdapterTypes(String apiKey) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permGetIptvAdapterTypes")) return null;

        return iptvManager.getAdapterTypes();
    }

    public ArrayList<TransponderData> getIptvTransponders(String apiKey) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permGetIptvTransponders")) return null;

        return iptvManager.getTransponders();
    }

    public ArrayList<CamData> getIptvCams(String apiKey) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permGetIptvCams")) return null;

        return iptvManager.getCams();
    }

    public ApiChannelData getIptvChannels(String login, String key) {
        if (!new NccAPI().checkPermission(login, key, "GetIptvChannels")) return null;

        NccIptvManager iptvManager = new NccIptvManager();

        ApiChannelData apiChannelData = new ApiChannelData();
        apiChannelData.data = iptvManager.getChannels();
        apiChannelData.status = 0;
        apiChannelData.message = "success";

        return apiChannelData;
    }

    public ArrayList<Integer> createIptvServer(String apiKey,
                                                Long serverIP,
                                                String serverSecret,
                                                Long serverLocalAddress,
                                                String serverComment,
                                                String serverName) {

        NccIptvManager iptvManager = new NccIptvManager();
        ServerData serverData = new ServerData();

        if (!new NccAPI().checkPermission(apiKey, "permCreateIptvServer")) return null;

        serverData.serverIP = serverIP;
        serverData.serverSecret = serverSecret;
        serverData.serverLocalAddress = serverLocalAddress;
        serverData.serverComment = serverComment;
        serverData.serverName = serverName;

        return iptvManager.createServer(serverData);
    }

    public ArrayList<Integer> createIptvAdapter(String apiKey,
                                                 Integer adapterDevice,
                                                 Integer adapterType,
                                                 Integer serverId,
                                                 String adapterComment) {
        NccIptvManager iptvManager = new NccIptvManager();
        AdapterData adapterData = new AdapterData();

        if (!new NccAPI().checkPermission(apiKey, "permCreateIptvAdapter")) return null;

        adapterData.adapterDevice = adapterDevice;
        adapterData.adapterType = adapterType;
        adapterData.serverId = serverId;
        adapterData.adapterComment = adapterComment;

        return iptvManager.createAdapter(adapterData);
    }

    public ArrayList<Integer> createIptvTransponder(String apiKey,
                                                     String transponderName,
                                                     Integer transponderFreq,
                                                     String transponderPolarity,
                                                     String transponderFEC,
                                                     Integer transponderSymbolrate,
                                                     String transponderType,
                                                     Integer adapterId,
                                                     String transponderLNB,
                                                     String transponderSat) {
        NccIptvManager iptvManager = new NccIptvManager();
        TransponderData transponderData = new TransponderData();

        if (!new NccAPI().checkPermission(apiKey, "permCreateIptvTransponder")) return null;

        transponderData.transponderName = transponderName;
        transponderData.transponderFreq = transponderFreq;
        transponderData.transponderPolarity = transponderPolarity;
        transponderData.transponderFEC = transponderFEC;
        transponderData.transponderSymbolrate = transponderSymbolrate;
        transponderData.transponderType = transponderType;
        transponderData.adapterId = adapterId;
        transponderData.transponderLNB = transponderLNB;
        transponderData.transponderSat = transponderSat;

        return iptvManager.createTransponder(transponderData);
    }

    public ArrayList<Integer> createIptvCam(String apiKey,
                                             String camServer,
                                             Integer camPort,
                                             String camUser,
                                             String camPassword,
                                             String camName,
                                             String camKey) {

        NccIptvManager iptvManager = new NccIptvManager();
        CamData camData = new CamData();

        if (!new NccAPI().checkPermission(apiKey, "permCreateIptvCam")) return null;

        camData.camServer = camServer;
        camData.camPort = camPort;
        camData.camUser = camUser;
        camData.camPassword = camPassword;
        camData.camName = camName;
        camData.camKey = camKey;

        return iptvManager.createCam(camData);
    }

    public ArrayList<Integer> createIptvChannel(String apiKey,
                                                 String channelName,
                                                 Integer channelTransponder,
                                                 Integer channelPnr,
                                                 Integer channelCam,
                                                 Long channelIP,
                                                 String channelComment) {

        NccIptvManager iptvManager = new NccIptvManager();
        ChannelData channelData = new ChannelData();

        if (!new NccAPI().checkPermission(apiKey, "permCreateIptvChannel")) return null;

        channelData.channelName = channelName;
        channelData.transponderId = channelTransponder;
        channelData.channelPnr = channelPnr;
        channelData.camId = channelCam;
        channelData.channelIP = channelIP;

        return iptvManager.createChannel(channelData);
    }

    public ArrayList<Integer> deleteIptvServer(String apiKey, Integer id) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permDeleteIptvServer")) return null;

        return iptvManager.deleteServer(id);
    }

    public ArrayList<Integer> deleteIptvAdapter(String apiKey, Integer id) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permDeleteIptvAdapter")) return null;

        return iptvManager.deleteAdapter(id);
    }

    public ArrayList<Integer> deleteIptvTransponder(String apiKey, Integer id) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permDeleteIptvTransponder")) return null;

        return iptvManager.deleteTransponder(id);
    }

    public ArrayList<Integer> deleteIptvCam(String apiKey, Integer id) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permDeleteIptvCam")) return null;

        return iptvManager.deleteCam(id);
    }

    public ArrayList<Integer> deleteIptvChannel(String apiKey, Integer id) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permDeleteIptvChannel")) return null;

        return iptvManager.deleteChannel(id);
    }

    public ArrayList<Integer> updateIptvServer(String apiKey,
                                                Integer id,
                                                Long serverIP,
                                                String serverSecret,
                                                Long serverLocalAddress,
                                                String serverComment,
                                                String serverName) {
        NccIptvManager iptvManager = new NccIptvManager();

        ServerData serverData = new ServerData();

        if (!new NccAPI().checkPermission(apiKey, "permUpdateIptvServer")) return null;

        serverData.id = id;
        serverData.serverIP = serverIP;
        serverData.serverSecret = serverSecret;
        serverData.serverLocalAddress = serverLocalAddress;
        serverData.serverComment = serverComment;
        serverData.serverName = serverName;

        return iptvManager.updateServer(serverData);
    }

    public ArrayList<Integer> updateIptvAdapter(String apiKey,
                                                 Integer id,
                                                 Integer adapterDevice,
                                                 Integer adapterType,
                                                 Integer serverId,
                                                 String adapterComment) {
        NccIptvManager iptvManager = new NccIptvManager();

        AdapterData adapterData = new AdapterData();

        if (!new NccAPI().checkPermission(apiKey, "permUpdateIptvAdapter")) return null;

        adapterData.id = id;
        adapterData.adapterDevice = adapterDevice;
        adapterData.adapterType = adapterType;
        adapterData.serverId = serverId;
        adapterData.adapterComment = adapterComment;

        return iptvManager.updateAdapter(adapterData);
    }

    public ArrayList<Integer> updateIptvTransponder(String apiKey,
                                                     Integer id,
                                                     String transponderName,
                                                     Integer transponderFreq,
                                                     String transponderPolarity,
                                                     String transponderFEC,
                                                     Integer transponderSymbolrate,
                                                     String transponderType,
                                                     Integer adapterId,
                                                     String transponderLNB,
                                                     String transponderSat) {
        NccIptvManager iptvManager = new NccIptvManager();

        TransponderData transponderData = new TransponderData();

        if (!new NccAPI().checkPermission(apiKey, "permUpdateIptvTransponder")) return null;

        transponderData.id = id;
        transponderData.transponderName = transponderName;
        transponderData.transponderFreq = transponderFreq;
        transponderData.transponderPolarity = transponderPolarity;
        transponderData.transponderFEC = transponderFEC;
        transponderData.transponderSymbolrate = transponderSymbolrate;
        transponderData.transponderType = transponderType;
        transponderData.adapterId = adapterId;
        transponderData.transponderLNB = transponderLNB;
        transponderData.transponderSat = transponderSat;

        return iptvManager.updateTransponder(transponderData);
    }

    public ArrayList<Integer> updateIptvCam(String apiKey,
                                             Integer id,
                                             String camServer,
                                             Integer camPort,
                                             String camUser,
                                             String camPassword,
                                             String camName,
                                             String camKey) {

        NccIptvManager iptvManager = new NccIptvManager();
        CamData camData = new CamData();

        if (!new NccAPI().checkPermission(apiKey, "permUpdateIptvCam")) return null;

        camData.id = id;
        camData.camServer = camServer;
        camData.camPort = camPort;
        camData.camUser = camUser;
        camData.camPassword = camPassword;
        camData.camName = camName;
        camData.camKey = camKey;

        return iptvManager.updateCam(camData);
    }

    public ArrayList<Integer> updateIptvChannel(String apiKey,
                                                 Integer id,
                                                 String channelName,
                                                 Integer channelPnr,
                                                 Integer transponderId,
                                                 Long channelIP,
                                                 Integer camId) {

        NccIptvManager iptvManager = new NccIptvManager();
        ChannelData channelData = new ChannelData();

        if (!new NccAPI().checkPermission(apiKey, "permUpdateIptvChannel")) return null;

        channelData.channelId = id;
        channelData.channelName = channelName;
        channelData.channelPnr = channelPnr;
        channelData.transponderId = transponderId;
        channelData.channelIP = channelIP;
        channelData.camId = camId;

        return iptvManager.updateChannel(channelData);
    }
}
