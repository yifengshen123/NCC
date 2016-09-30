package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

public class TransponderData extends NccAbstractData<TransponderData> {
    public Integer id;
    public String transponderName;
    public Integer transponderFreq;
    public String transponderPolarity;
    public String transponderFEC;
    public Integer transponderSymbolrate;
    public String transponderType;
    public Integer adapterId;
    public Integer adapterDevice;
    public String adapterCard;
    public String adapterChip;
    public Integer serverId;
    public Long serverIP;
    public String serverSecret;
    public Long serverLocalAddress;
    public String serverName;
    public String transponderLNB;
    public String transponderSat;
    public TransponderStatus transponderStatus;

    @Override
    public TransponderData fillData(){
        TransponderData transponderData = new TransponderData();
        NccIptvManager iptvManager = new NccIptvManager();

        try {
            transponderData.id = rs.getInt("id");
            transponderData.transponderName = rs.getString("transponderName");
            transponderData.transponderFreq = rs.getInt("transponderFreq");
            transponderData.transponderPolarity = rs.getString("transponderPolarity");
            transponderData.transponderFEC = rs.getString("transponderFEC");
            transponderData.transponderSymbolrate = rs.getInt("transponderSymbolrate");
            transponderData.transponderType = rs.getString("transponderType");
            transponderData.adapterId = rs.getInt("adapterId");
            transponderData.adapterDevice = rs.getInt("adapterDevice");
            transponderData.adapterCard = rs.getString("adapterCard");
            transponderData.adapterChip = rs.getString("adapterChip");
            transponderData.serverId = rs.getInt("serverId");
            transponderData.serverIP = rs.getLong("serverIP");
            transponderData.serverSecret = rs.getString("serverSecret");
            transponderData.serverLocalAddress = rs.getLong("serverLocalAddress");
            transponderData.serverName = rs.getString("serverName");
            transponderData.transponderLNB = rs.getString("transponderLNB");
            transponderData.transponderSat = rs.getString("transponderSat");
            transponderData.transponderStatus = iptvManager.getTransponderStatus(transponderData.id);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transponderData;
    }
}
