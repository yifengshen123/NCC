package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

public class AdapterData extends NccAbstractData<AdapterData> {
    public Integer id;
    public Integer adapterDevice;
    public Integer adapterType;
    public Integer serverId;
    public String adapterComment;
    public Integer adapterSat;
    public Long serverIP;
    public String serverName;
    public String satName;
    public String cardName;
    public String chipName;

    @Override
    public AdapterData fillData() {
        AdapterData adapterData = new AdapterData();

        try {
            adapterData.id = rs.getInt("id");
            adapterData.adapterDevice = rs.getInt("adapterDevice");
            adapterData.adapterType = rs.getInt("adapterType");
            adapterData.serverId = rs.getInt("serverId");
            adapterData.adapterComment = rs.getString("adapterComment");
            adapterData.adapterSat = rs.getInt("adapterSat");
            adapterData.serverIP = rs.getLong("serverIP");
            adapterData.serverName = rs.getString("serverName");
            adapterData.satName = rs.getString("satName");
            adapterData.cardName = rs.getString("cardName");
            adapterData.chipName = rs.getString("chipName");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return adapterData;
    }
}
