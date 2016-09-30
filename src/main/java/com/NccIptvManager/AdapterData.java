package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

public class AdapterData extends NccAbstractData<AdapterData> {
    public Integer id;
    public Integer adapterDevice;
    public Integer adapterType;
    public Integer serverId;
    public String adapterComment;

    @Override
    public AdapterData fillData() {
        AdapterData adapterData = new AdapterData();

        try {
            adapterData.id = rs.getInt("id");
            adapterData.adapterDevice = rs.getInt("adapterDevice");
            adapterData.adapterType = rs.getInt("adapterType");
            adapterData.serverId = rs.getInt("serverId");
            adapterData.adapterComment = rs.getString("adapterComment");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return adapterData;
    }
}
