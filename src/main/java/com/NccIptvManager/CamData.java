package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

public class CamData extends NccAbstractData<CamData> {
    public Integer id;
    public String camServer;
    public Integer camPort;
    public String camUser;
    public String camPassword;
    public String camName;
    public String camKey;

    @Override
    public CamData fillData() {
        CamData camData = new CamData();

        try {
            camData.id = rs.getInt("id");
            camData.camName = rs.getString("camName");
            camData.camServer = rs.getString("camServer");
            camData.camPort = rs.getInt("camPort");
            camData.camUser = rs.getString("camUser");
            camData.camPassword = rs.getString("camPassword");
            camData.camKey = rs.getString("camKey");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return camData;
    }
}
