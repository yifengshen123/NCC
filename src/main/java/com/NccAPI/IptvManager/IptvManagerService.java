package com.NccAPI.IptvManager;

import com.NccIptvManager.*;

import java.util.ArrayList;

public interface IptvManagerService {
    public Integer runIptvTransponder(String apiKey, Integer id);
    public ArrayList<Integer> stopIptvTransponder(String apiKey, Integer id);
    public TransponderStatus getIptvTransponderStatus(String apiKey, Integer id);

    public ArrayList<ServerData> getIptvServers(String apiKey);
    public ArrayList<AdapterData> getIptvAdapters(String apiKey);
    public ArrayList<AdapterType> getIptvAdapterTypes(String apiKey);
    public ArrayList<AdapterData> getIptvAdaptersByServerId(String apiKey, Integer id);
    public ArrayList<TransponderData> getIptvTransponders(String apiKey);
    public ArrayList<CamData> getIptvCams(String apiKey);
    public ApiChannelData getIptvChannels(String login, String key);

    public ArrayList<Integer> createIptvServer(String apiKey,
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

    public ArrayList<Integer> createIptvTransponder(String apiKey,
                                                     String transponderName,
                                                     Integer transponderFreq,
                                                     String transponderPolarity,
                                                     String transponderFEC,
                                                     Integer transponderSymbolrate,
                                                     String transponderType,
                                                     Integer adapterId,
                                                     String transponderLNB,
                                                     String transponderSat);

    public ArrayList<Integer> createIptvCam(String apiKey,
                                             String camServer,
                                             Integer camPort,
                                             String camUser,
                                             String camPassword,
                                             String camName,
                                             String camKey);

    public ArrayList<Integer> createIptvChannel(String apiKey,
                                             String channelName,
                                             Integer channelTransponder,
                                             Integer channelPnr,
                                             Integer channelCam,
                                             Long channelIP,
                                             String channelComment);

    public ArrayList<Integer> updateIptvServer(String apiKey,
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
                                                     String transponderSat);

    public ArrayList<Integer> updateIptvCam(String apiKey,
                                             Integer id,
                                             String camServer,
                                             Integer camPort,
                                             String camUser,
                                             String camPassword,
                                             String camName,
                                             String camKey);

    public ArrayList<Integer> updateIptvChannel(String apiKey,
                                             Integer id,
                                             String channelName,
                                             Integer channelPnr,
                                             Integer transponderId,
                                             Long channelIP,
                                             Integer camId);

    public ArrayList<Integer> deleteIptvServer(String apiKey, Integer id);
    public ArrayList<Integer> deleteIptvAdapter(String apiKey, Integer id);
    public ArrayList<Integer> deleteIptvTransponder(String apiKey, Integer id);
    public ArrayList<Integer> deleteIptvCam(String apiKey, Integer id);
    public ArrayList<Integer> deleteIptvChannel(String apiKey, Integer id);

}
