package com.NccAPI.IptvManager;

import com.NccIptvManager.*;

import java.util.ArrayList;

public interface IptvManagerService {
    public Integer runIptvTransponder(String login, String key,
                                      Integer id);
    public ArrayList<Integer> stopIptvTransponder(String apiKey, Integer id);
    public TransponderStatus getIptvTransponderStatus(String apiKey, Integer id);

    public ApiSymbolRates getIptvSymbolRates(String login, String key);
    public ApiLnbTypes getIptvLnbTypes(String login, String key);
    public ApiFecTypes getIptvFecTypes(String login, String key);
    public ApiPolarityTypes getIptvPolarityTypes(String login, String key);
    public ApiTransponderTypes getIptvTransponderTypes(String login, String key);
    public ApiSatData getIptvSat(String login, String key);

    public ApiServerData getIptvServers(String login, String key);
    public ApiAdapterData getIptvAdapters(String login, String key);
    public ArrayList<AdapterType> getIptvAdapterTypes(String apiKey);
    public ArrayList<AdapterData> getIptvAdaptersByServerId(String apiKey, Integer id);
    public ApiTransponderData getIptvTransponders(String login, String key);
    public ApiCamData getIptvCams(String login, String key);
    public ApiChannelData getIptvChannels(String login, String key);
    public ApiChannelData getIptvChannelById(String login, String key, Integer id);

    public ApiServerData createIptvServer(String login, String key,
                                                Long serverIP,
                                                String serverSecret,
                                                Long serverLocalAddress,
                                                String serverComment,
                                                String serverName);

    public ArrayList<Integer> createIptvAdapter(String apiKey,
                                                 Integer adapterDevice,
                                                 Integer adapterType,
                                                 Integer serverId,
                                                 String adapterComment);

    public ApiTransponderData createIptvTransponder(String login, String key,
                                                     String transponderName,
                                                     Integer transponderFreq,
                                                     String transponderPolarity,
                                                     String transponderFEC,
                                                     Integer transponderSymbolrate,
                                                     String transponderType,
                                                     Integer adapterId,
                                                     String transponderLNB,
                                                     Integer transponderSat);

    public ArrayList<Integer> createIptvCam(String apiKey,
                                             String camServer,
                                             Integer camPort,
                                             String camUser,
                                             String camPassword,
                                             String camName,
                                             String camKey);

    public ApiChannelData createIptvChannel(String login, String key,
                                             String channelName,
                                             Integer channelPnr,
                                             Integer channelTransponder,
                                             Long channelIP,
                                             Integer channelCam);

    public ApiServerData updateIptvServer(String login, String key,
                                                Integer id,
                                                Long serverIP,
                                                String serverSecret,
                                                Long serverLocalAddress,
                                                String serverComment,
                                                String serverName);


    public ArrayList<Integer> updateIptvAdapter(String apiKey,
                                                 Integer id,
                                                 Integer adapterDevice,
                                                 Integer adapterType,
                                                 Integer serverId,
                                                 String adapterComment);

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
                                                     Integer transponderSat);

    public ArrayList<Integer> updateIptvCam(String apiKey,
                                             Integer id,
                                             String camServer,
                                             Integer camPort,
                                             String camUser,
                                             String camPassword,
                                             String camName,
                                             String camKey);

    public ApiChannelData updateIptvChannel(String login, String key,
                                             Integer id,
                                             String channelName,
                                             Integer channelPnr,
                                             Integer transponderId,
                                             Long channelIP,
                                             Integer camId);

    public ApiServerData deleteIptvServer(String login, String key,
                                               Integer id);
    public ArrayList<Integer> deleteIptvAdapter(String apiKey, Integer id);
    public ApiTransponderData deleteIptvTransponder(String login, String key,
                                                    Integer id);
    public ArrayList<Integer> deleteIptvCam(String apiKey, Integer id);
    public ApiChannelData deleteIptvChannel(String login, String key, Integer id);

}
