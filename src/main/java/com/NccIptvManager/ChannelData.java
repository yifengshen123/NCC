package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

public class ChannelData extends NccAbstractData<ChannelData> {
    public Integer channelId;
    public String channelName;
    public Integer channelPnr;
    public Integer transponderId;
    public Long channelIP;
    public Integer currentState;
    public Integer currentBitrate;
    public Integer currentCC;
    public Integer currentPES;
    public Integer lastActive;

    public Integer camId;
    public String camName;
    public String camServer;
    public Integer camPort;
    public String camUser;
    public String camPassword;
    public String camKey;

    public String transponderName;
    public String transponderSat;
    public Integer transponderFreq;
    public String transponderPolarity;
    public String transponderFEC;
    public Integer transponderSymbolrate;
    public String transponderType;

    public Integer adapterDevice;
    public String adapterType;
    public String adapterCard;

    public Long serverIP;
    public String serverSecret;
    public String serverName;
    public Long serverLocalAddress;

    @Override
    public ChannelData fillData(){
        ChannelData channelData = new ChannelData();

        try {
            channelData.channelId = rs.getInt("channelId");
            channelData.channelName = rs.getString("channelName");
            channelData.channelPnr = rs.getInt("channelPnr");
            channelData.transponderId = rs.getInt("transponderId");
            channelData.channelIP = rs.getLong("channelIP");
            channelData.currentState = rs.getInt("currentState");
            channelData.currentBitrate = rs.getInt("currentBitrate");
            channelData.currentCC = rs.getInt("currentCC");
            channelData.currentPES = rs.getInt("currentPES");
            channelData.lastActive = rs.getInt("lastActive");

            channelData.camName = rs.getString("camName");
            channelData.camId = rs.getInt("camId");
            channelData.camServer = rs.getString("camServer");
            channelData.camPort = rs.getInt("camPort");
            channelData.camUser = rs.getString("camUser");
            channelData.camPassword = rs.getString("camPassword");
            channelData.camKey = rs.getString("camKey");

            channelData.transponderName = rs.getString("transponderName");
            channelData.transponderSat = rs.getString("transponderSat");
            channelData.transponderFreq = rs.getInt("transponderFreq");
            channelData.transponderPolarity = rs.getString("transponderPolarity");
            channelData.transponderFEC = rs.getString("transponderFEC");
            channelData.transponderSymbolrate = rs.getInt("transponderSymbolrate");
            channelData.transponderType = rs.getString("transponderType");

            channelData.adapterDevice = rs.getInt("adapterDevice");
            channelData.adapterType = rs.getString("adapterType");
            channelData.adapterCard = rs.getString("adapterCard");

            channelData.serverIP = rs.getLong("serverIP");
            channelData.serverSecret = rs.getString("serverSecret");
            channelData.serverName = rs.getString("serverName");
            channelData.serverLocalAddress = rs.getLong("serverLocalAddress");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return channelData;
    }
}
