package com.NccAPI.IptvManager;

import com.NccAPI.NccAPI;
import com.NccIptvManager.*;

import java.util.ArrayList;
import java.util.HashMap;

public class IptvManagerImpl implements IptvManagerService {

    public ApiTransponderLock getIptvTransponderLock(String login, String key,
                                                     Integer id) {

        ApiTransponderLock apiTransponderLock = new ApiTransponderLock();

        apiTransponderLock.data = new ArrayList<>();
        apiTransponderLock.status = 1;
        apiTransponderLock.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetIptvTransponderLock")) {
            apiTransponderLock.message = "Permission denied";
            return apiTransponderLock;
        }

        TransponderLockData data = new NccIptvManager().getTransponderLock(id);

        if (data != null) {
            apiTransponderLock.data.add(data);
            apiTransponderLock.status = 0;
            apiTransponderLock.message = "success";
        }

        return apiTransponderLock;
    }

    public ApiTransponderAction runIptvTransponder(String login, String key,
                                                   Integer id) {

        final NccIptvManager iptvManager = new NccIptvManager();
        final Integer transponderId = id;

        ApiTransponderAction apiTransponderAction = new ApiTransponderAction();

        apiTransponderAction.status = 1;
        apiTransponderAction.message = "error";

        if (!new NccAPI().checkPermission(login, key, "RunIptvTransponder")) {
            apiTransponderAction.message = "Permission denied";
            return apiTransponderAction;
        }

        iptvManager.stopTransponder(id);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                iptvManager.runTransponder(transponderId);
            }
        });
        t.start();

        apiTransponderAction.status = 0;
        apiTransponderAction.message = "success";

        return apiTransponderAction;
    }

    public ApiTransponderAction stopIptvTransponder(String login, String key,
                                                    Integer id) {

        ApiTransponderAction apiTransponderAction = new ApiTransponderAction();

        apiTransponderAction.status = 1;
        apiTransponderAction.message = "error";

        if (!new NccAPI().checkPermission(login, key, "StopIptvTransponder")) {
            apiTransponderAction.message = "Permission denied";
            return apiTransponderAction;
        }

        ArrayList<Integer> data = new NccIptvManager().stopTransponder(id);

        if (data != null) {
            if (data.size() > 0) {
                apiTransponderAction.status = 0;
                apiTransponderAction.message = "success";
            }
        }

        return apiTransponderAction;
    }

    public ApiSymbolRates getIptvSymbolRates(String login, String key) {
        ApiSymbolRates apiSymbolRates = new ApiSymbolRates();

        apiSymbolRates.data = new NccIptvManager().getSymbolRates();
        apiSymbolRates.status = 0;
        apiSymbolRates.message = "success";

        return apiSymbolRates;
    }

    public ApiLnbTypes getIptvLnbTypes(String login, String key) {
        ApiLnbTypes apiLnbTypes = new ApiLnbTypes();

        apiLnbTypes.data = new NccIptvManager().getLnbTypes();
        apiLnbTypes.status = 0;
        apiLnbTypes.message = "success";

        return apiLnbTypes;
    }

    public ApiFecTypes getIptvFecTypes(String login, String key) {
        ApiFecTypes apiFecTypes = new ApiFecTypes();

        apiFecTypes.data = new NccIptvManager().getFecTypes();
        apiFecTypes.status = 0;
        apiFecTypes.message = "success";

        return apiFecTypes;
    }

    public ApiPolarityTypes getIptvPolarityTypes(String login, String key) {
        ApiPolarityTypes apiPolarityTypes = new ApiPolarityTypes();

        apiPolarityTypes.data = new NccIptvManager().getPolarityTypes();
        apiPolarityTypes.status = 0;
        apiPolarityTypes.message = "success";

        return apiPolarityTypes;
    }

    public ApiTransponderTypes getIptvTransponderTypes(String login, String key) {
        ApiTransponderTypes apiTransponderTypes = new ApiTransponderTypes();

        apiTransponderTypes.data = new NccIptvManager().getTransponderTypes();
        apiTransponderTypes.status = 0;
        apiTransponderTypes.message = "success";

        return apiTransponderTypes;
    }

    public ApiAdapterData getIptvAdapters(String login, String key) {
        ApiAdapterData apiAdapterData = new ApiAdapterData();

        if (!new NccAPI().checkPermission(login, key, "GetIptvAdapters")) {
            apiAdapterData.data = new ArrayList<>();
            apiAdapterData.status = 1;
            apiAdapterData.message = "Permission denied";
            return apiAdapterData;
        }

        apiAdapterData.data = new NccIptvManager().getAdapters();
        apiAdapterData.status = 0;
        apiAdapterData.message = "success";

        return apiAdapterData;
    }

    public TransponderStatus getIptvTransponderStatus(String apiKey, Integer id) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permIptvGetTransponderStatus")) return null;

        return iptvManager.getTransponderStatus(id);
    }

    public ApiServerData getIptvServers(String login, String key) {

        ApiServerData apiServerData = new ApiServerData();

        apiServerData.data = new ArrayList<>();
        apiServerData.status = 1;
        apiServerData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetIptvServers")) {
            apiServerData.message = "Permission denied";
            return apiServerData;
        }

        ArrayList<ServerData> data = new NccIptvManager().getServers();

        if (data != null) {
            apiServerData.data = data;
            apiServerData.status = 0;
            apiServerData.message = "success";
        }

        return apiServerData;
    }

    public ArrayList<AdapterData> getIptvAdaptersByServerId(String apiKey, Integer id) {
        NccIptvManager iptvManager = new NccIptvManager();

        if (!new NccAPI().checkPermission(apiKey, "permGetIptvAdaptersByServerId")) return null;

        return iptvManager.getAdaptersByServerId(id);
    }

    public ApiAdapterTypes getIptvAdapterTypes(String login, String key) {

        ApiAdapterTypes apiAdapterTypes = new ApiAdapterTypes();
        apiAdapterTypes.data = new ArrayList<>();
        apiAdapterTypes.status = 1;
        apiAdapterTypes.message = "error";

        ArrayList<AdapterType> data = new NccIptvManager().getAdapterTypes();

        if (data != null) {
            apiAdapterTypes.data = data;
            apiAdapterTypes.status = 0;
            apiAdapterTypes.message = "success";
        }

        return apiAdapterTypes;
    }

    public ApiSatData getIptvSat(String login, String key) {
        ApiSatData apiSatData = new ApiSatData();

        apiSatData.data = new NccIptvManager().getSat();
        apiSatData.status = 0;
        apiSatData.message = "success";

        return apiSatData;
    }

    public ApiTransponderData getIptvTransponders(String login, String key) {

        ApiTransponderData apiTransponderData = new ApiTransponderData();
        apiTransponderData.data = new ArrayList<>();
        apiTransponderData.status = 1;
        apiTransponderData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetIptvTransponders")) {
            apiTransponderData.message = "Permission denied";
            return apiTransponderData;
        }

        ArrayList<TransponderData> transponders = new NccIptvManager().getTransponders();
        if (transponders != null) {
            apiTransponderData.data = transponders;
            apiTransponderData.status = 0;
            apiTransponderData.message = "success";
        }

        return apiTransponderData;
    }

    public ApiCamData getIptvCams(String login, String key) {

        ApiCamData apiCamData = new ApiCamData();
        apiCamData.data = new ArrayList<>();
        apiCamData.status = 1;
        apiCamData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetIptvCams")) {
            apiCamData.message = "Permission denied";
            return apiCamData;
        }

        ArrayList<CamData> cams = new NccIptvManager().getCam();
        if (cams != null) {
            apiCamData.data = cams;
            apiCamData.status = 0;
            apiCamData.message = "success";
        }

        return apiCamData;
    }

    public ApiChannelData getIptvChannels(String login, String key) {
        if (!new NccAPI().checkPermission(login, key, "GetIptvChannels")) return null;

        ApiChannelData apiChannelData = new ApiChannelData();
        apiChannelData.data = new ArrayList<ChannelData>();
        apiChannelData.status = 1;
        apiChannelData.message = "error";

        ArrayList<ChannelData> channels = new NccIptvManager().getChannels();

        if (channels != null) {
            if (channels.size() > 0) {
                apiChannelData.data = channels;
                apiChannelData.status = 0;
                apiChannelData.message = "success";
            }
        }

        return apiChannelData;
    }

    public ApiChannelData getIptvChannelsByTransponder(String login, String key,
                                                       Integer id) {

        ApiChannelData apiChannelData = new ApiChannelData();

        apiChannelData.data = new ArrayList<>();
        apiChannelData.status = 1;
        apiChannelData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetIptvChannels")) {
            apiChannelData.message = "Permission denied";
            return apiChannelData;
        }

        ArrayList<ChannelData> data = new NccIptvManager().getChannelsByTransponder(id);

        if (data != null) {
            apiChannelData.data = data;
            apiChannelData.status = 0;
            apiChannelData.message = "success";
        }

        return apiChannelData;
    }

    public ApiChannelData getIptvChannelById(String login, String key, Integer id) {

        if (!new NccAPI().checkPermission(login, key, "GetIptvChannels")) return null;

        ApiChannelData apiChannelData = new ApiChannelData();
        apiChannelData.data = new ArrayList<ChannelData>();
        apiChannelData.status = 1;
        apiChannelData.message = "error";

        ChannelData channelData = new NccIptvManager().getChannelById(id);

        if (channelData != null) {

            if (channelData.channelId > 0) {
                apiChannelData.data = new ArrayList<ChannelData>();
                apiChannelData.data.add(channelData);
                apiChannelData.status = 0;
                apiChannelData.message = "success";
            }
        }

        return apiChannelData;
    }

    public ApiServerData createIptvServer(String login, String key,
                                          Long serverIP,
                                          String serverSecret,
                                          Long serverLocalAddress,
                                          String serverComment,
                                          String serverName) {

        ApiServerData apiServerData = new ApiServerData();

        apiServerData.data = new ArrayList<>();
        apiServerData.status = 1;
        apiServerData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "CreateIptvServer")) {
            apiServerData.message = "Permission denied";
            return apiServerData;
        }

        ServerData serverData = new ServerData();

        serverData.serverIP = serverIP;
        serverData.serverSecret = serverSecret;
        serverData.serverLocalAddress = serverLocalAddress;
        serverData.serverComment = serverComment;
        serverData.serverName = serverName;

        ServerData data = new NccIptvManager().createServer(serverData);

        if (data != null) {
            apiServerData.data.add(serverData);
            apiServerData.status = 0;
            apiServerData.message = "success";
        }

        return apiServerData;
    }

    public ApiAdapterData createIptvAdapter(String login, String key,
                                            Integer adapterDevice,
                                            Integer adapterType,
                                            Integer serverId,
                                            Integer adapterSat,
                                            String adapterComment) {

        ApiAdapterData apiAdapterData = new ApiAdapterData();

        apiAdapterData.data = new ArrayList<>();
        apiAdapterData.status = 1;
        apiAdapterData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "CreateIptvAdapter")) {
            apiAdapterData.message = "Permission denied";
            return apiAdapterData;
        }

        AdapterData adapterData = new AdapterData();

        adapterData.adapterDevice = adapterDevice;
        adapterData.adapterType = adapterType;
        adapterData.serverId = serverId;
        adapterData.adapterSat = adapterSat;
        adapterData.adapterComment = adapterComment;

        AdapterData data = new NccIptvManager().createAdapter(adapterData);

        if (data != null) {
            apiAdapterData.data.add(data);
            apiAdapterData.status = 0;
            apiAdapterData.message = "success";
        }

        return apiAdapterData;
    }

    public ApiTransponderData createIptvTransponder(String login, String key,
                                                    String transponderName,
                                                    Integer transponderFreq,
                                                    String transponderPolarity,
                                                    String transponderFEC,
                                                    Integer transponderSymbolrate,
                                                    String transponderType,
                                                    Integer adapterId,
                                                    String transponderLNB,
                                                    Integer transponderSat) {

        ApiTransponderData apiTransponderData = new ApiTransponderData();

        apiTransponderData.data = new ArrayList<>();
        apiTransponderData.status = 1;
        apiTransponderData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "CreateIptvTransponder")) {
            apiTransponderData.status = 1;
            apiTransponderData.message = "Permission denied";

            return apiTransponderData;
        }

        TransponderData transponderData = new TransponderData();

        transponderData.transponderName = transponderName;
        transponderData.transponderFreq = transponderFreq;
        transponderData.transponderPolarity = transponderPolarity;
        transponderData.transponderFEC = transponderFEC;
        transponderData.transponderSymbolrate = transponderSymbolrate;
        transponderData.transponderType = transponderType;
        transponderData.adapterId = adapterId;
        transponderData.transponderLNB = transponderLNB;
        transponderData.transponderSat = transponderSat;

        TransponderData data = new NccIptvManager().createTransponder(transponderData);

        if (data != null) {
            apiTransponderData.data.add(data);
            apiTransponderData.status = 0;
            apiTransponderData.message = "success";
        }

        return apiTransponderData;
    }

    public ApiCamData createIptvCam(String login, String key,
                                    String camServer,
                                    Integer camPort,
                                    String camUser,
                                    String camPassword,
                                    String camName,
                                    String camKey) {

        ApiCamData apiCamData = new ApiCamData();

        apiCamData.data = new ArrayList<>();
        apiCamData.status = 1;
        apiCamData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "CreateIptvCam")) {
            apiCamData.message = "Permission denied";
            return apiCamData;
        }

        CamData camData = new CamData();

        camData.camServer = camServer;
        camData.camPort = camPort;
        camData.camUser = camUser;
        camData.camPassword = camPassword;
        camData.camName = camName;
        camData.camKey = camKey;

        CamData data = new NccIptvManager().createCam(camData);

        if (data != null) {
            apiCamData.data.add(camData);
            apiCamData.status = 0;
            apiCamData.message = "success";
        }

        return apiCamData;
    }

    public ApiChannelData createIptvChannel(String login, String key,
                                            String channelName,
                                            Integer channelPnr,
                                            Integer channelTransponder,
                                            Long channelIP,
                                            Integer channelCam) {

        ApiChannelData apiChannelData = new ApiChannelData();
        apiChannelData.data = new ArrayList<>();
        apiChannelData.status = 1;
        apiChannelData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "createIptvChannel")) {
            apiChannelData.message = "Permission denied";
            return apiChannelData;
        }

        ChannelData channelData = new ChannelData();
        channelData.channelName = channelName;
        channelData.transponderId = channelTransponder;
        channelData.channelPnr = channelPnr;
        channelData.camId = channelCam;
        channelData.channelIP = channelIP;

        ChannelData data = new NccIptvManager().createChannel(channelData);
        if (data != null) {
            apiChannelData.data.add(data);
            apiChannelData.status = 0;
            apiChannelData.message = "success";
        }

        return apiChannelData;
    }

    public ApiServerData deleteIptvServer(String login, String key,
                                          Integer id) {

        ApiServerData apiServerData = new ApiServerData();

        apiServerData.data = new ArrayList<>();
        apiServerData.status = 1;
        apiServerData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "DeleteIptvServer")) {
            apiServerData.message = "Permission denied";
            return apiServerData;
        }

        if (new NccIptvManager().deleteServer(id)) {
            apiServerData.status = 0;
            apiServerData.message = "success";
        }

        return apiServerData;
    }

    public ApiAdapterData deleteIptvAdapter(String login, String key,
                                            Integer id) {

        ApiAdapterData apiAdapterData = new ApiAdapterData();

        apiAdapterData.data = new ArrayList<>();
        apiAdapterData.status = 1;
        apiAdapterData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "DeleteIptvAdapter")) {
            apiAdapterData.message = "Permission denied";
            return apiAdapterData;
        }

        if (new NccIptvManager().deleteAdapter(id)) {
            apiAdapterData.status = 0;
            apiAdapterData.message = "success";
        }

        return apiAdapterData;
    }

    public ApiTransponderData deleteIptvTransponder(String login, String key,
                                                    Integer id) {

        ApiTransponderData apiTransponderData = new ApiTransponderData();

        apiTransponderData.data = new ArrayList<>();
        apiTransponderData.status = 1;
        apiTransponderData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "DeleteIptvTransponder")) {
            apiTransponderData.message = "Permission denied";
            return apiTransponderData;
        }

        if (new NccIptvManager().deleteTransponder(id)) {
            apiTransponderData.status = 0;
            apiTransponderData.message = "success";
        }

        return apiTransponderData;
    }

    public ApiCamData deleteIptvCam(String login, String key,
                                    Integer id) {

        ApiCamData apiCamData = new ApiCamData();

        apiCamData.data = new ArrayList<>();
        apiCamData.status = 1;
        apiCamData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "DeleteIptvCam")) {
            apiCamData.message = "Permission denied";
            return apiCamData;
        }

        if (new NccIptvManager().deleteCam(id)) {
            apiCamData.status = 0;
            apiCamData.message = "success";
        }

        return apiCamData;
    }

    public ApiChannelData deleteIptvChannel(String login, String key,
                                            Integer id) {

        ApiChannelData apiChannelData = new ApiChannelData();
        apiChannelData.data = new ArrayList<>();
        apiChannelData.status = 1;
        apiChannelData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "DeleteIptvChannel")) {
            apiChannelData.message = "Permission denied";
            return apiChannelData;
        }

        if (new NccIptvManager().deleteChannel(id)) {
            apiChannelData.status = 0;
            apiChannelData.message = "success";
        }

        return apiChannelData;
    }

    public ApiServerData updateIptvServer(String login, String key,
                                          Integer id,
                                          Long serverIP,
                                          String serverSecret,
                                          Long serverLocalAddress,
                                          String serverComment,
                                          String serverName) {

        ApiServerData apiServerData = new ApiServerData();
        apiServerData.data = new ArrayList<>();
        apiServerData.status = 1;
        apiServerData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "UpdateIptvServer")) {
            apiServerData.message = "Permission denied";
            return apiServerData;
        }

        ServerData serverData = new ServerData();

        serverData.id = id;
        serverData.serverIP = serverIP;
        serverData.serverSecret = serverSecret;
        serverData.serverLocalAddress = serverLocalAddress;
        serverData.serverComment = serverComment;
        serverData.serverName = serverName;

        ServerData data = new NccIptvManager().updateServer(serverData);

        if (data != null) {
            apiServerData.data.add(serverData);
            apiServerData.status = 0;
            apiServerData.message = "success";
        }

        return apiServerData;
    }

    public ApiAdapterData updateIptvAdapter(String login, String key,
                                            Integer id,
                                            Integer adapterDevice,
                                            Integer adapterType,
                                            Integer serverId,
                                            Integer adapterSat,
                                            String adapterComment) {

        ApiAdapterData apiAdapterData = new ApiAdapterData();

        apiAdapterData.data = new ArrayList<>();
        apiAdapterData.status = 1;
        apiAdapterData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "UpdateIptvAdapter")) {
            apiAdapterData.message = "Permission denied";
            return apiAdapterData;
        }

        AdapterData adapterData = new AdapterData();

        adapterData.id = id;
        adapterData.adapterDevice = adapterDevice;
        adapterData.adapterType = adapterType;
        adapterData.serverId = serverId;
        adapterData.adapterSat = adapterSat;
        adapterData.adapterComment = adapterComment;

        AdapterData data = new NccIptvManager().updateAdapter(adapterData);

        if (data != null) {
            apiAdapterData.data.add(data);
            apiAdapterData.status = 0;
            apiAdapterData.message = "success";
        }

        return apiAdapterData;
    }

    public ApiTransponderData updateIptvTransponder(String login, String key,
                                                    Integer id,
                                                    String transponderName,
                                                    Integer transponderFreq,
                                                    String transponderPolarity,
                                                    String transponderFEC,
                                                    Integer transponderSymbolrate,
                                                    String transponderType,
                                                    Integer adapterId,
                                                    String transponderLNB,
                                                    Integer transponderSat) {

        ApiTransponderData apiTransponderData = new ApiTransponderData();

        apiTransponderData.data = new ArrayList<>();
        apiTransponderData.status = 1;
        apiTransponderData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "UpdateIptvTransponder")) {
            apiTransponderData.status = 1;
            apiTransponderData.message = "Permission denied";

            return apiTransponderData;
        }

        TransponderData transponderData = new TransponderData();

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

        TransponderData data = new NccIptvManager().updateTransponder(transponderData);

        if (data != null) {
            apiTransponderData.data.add(data);
            apiTransponderData.status = 0;
            apiTransponderData.message = "success";
        }

        return apiTransponderData;
    }

    public ApiCamData updateIptvCam(String login, String key,
                                    Integer id,
                                    String camServer,
                                    Integer camPort,
                                    String camUser,
                                    String camPassword,
                                    String camName,
                                    String camKey) {

        ApiCamData apiCamData = new ApiCamData();

        apiCamData.data = new ArrayList<>();
        apiCamData.status = 1;
        apiCamData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "UpdateIptvCam")) {
            apiCamData.message = "Permission denied";
            return apiCamData;
        }

        CamData camData = new CamData();

        camData.id = id;
        camData.camServer = camServer;
        camData.camPort = camPort;
        camData.camUser = camUser;
        camData.camPassword = camPassword;
        camData.camName = camName;
        camData.camKey = camKey;

        CamData data = new NccIptvManager().updateCam(camData);

        if (data != null) {
            apiCamData.data.add(camData);
            apiCamData.status = 0;
            apiCamData.message = "success";
        }

        return apiCamData;
    }

    public ApiChannelData updateIptvChannel(String login, String key,
                                            Integer id,
                                            String channelName,
                                            Integer channelPnr,
                                            Integer transponderId,
                                            Long channelIP,
                                            Integer camId) {


        ApiChannelData apiChannelData = new ApiChannelData();
        apiChannelData.data = new ArrayList<>();
        apiChannelData.status = 1;
        apiChannelData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "UpdateIptvChannel")) {
            apiChannelData.message = "Permission denied";
            return apiChannelData;
        }

        ChannelData channelData = new ChannelData();
        channelData.channelId = id;
        channelData.channelName = channelName;
        channelData.channelPnr = channelPnr;
        channelData.transponderId = transponderId;
        channelData.channelIP = channelIP;
        channelData.camId = camId;

        ChannelData data = new NccIptvManager().updateChannel(channelData);
        System.out.println(data);
        if (data != null) {
            apiChannelData.status = 0;
            apiChannelData.message = "success";
            apiChannelData.data.add(data);
        }

        return apiChannelData;
    }
}
