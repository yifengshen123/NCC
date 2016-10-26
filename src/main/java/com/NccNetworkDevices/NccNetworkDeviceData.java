package com.NccNetworkDevices;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 05.10.16.
 */
public class NccNetworkDeviceData extends NccAbstractData<NccNetworkDeviceData> {

    public Integer id;
    public Long deviceIP;
    public String deviceName;
    public Integer deviceType;
    public String snmpCommunity;
    public String addressStreet;
    public String addressBuild;
    public String typeName;

    @Override
    public NccNetworkDeviceData fillData(){
        NccNetworkDeviceData networkDeviceData = new NccNetworkDeviceData();

        try {
            networkDeviceData.id = rs.getInt("id");
            networkDeviceData.deviceIP = rs.getLong("deviceIP");
            networkDeviceData.deviceName = rs.getString("deviceName");
            networkDeviceData.deviceType = rs.getInt("deviceType");
            networkDeviceData.snmpCommunity = rs.getString("snmpCommunity");
            networkDeviceData.addressStreet = rs.getString("addressStreet");
            networkDeviceData.addressBuild = rs.getString("addressBuild");
            networkDeviceData.typeName = rs.getString("typeName");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return networkDeviceData;
    }
}
