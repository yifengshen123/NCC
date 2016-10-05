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

    @Override
    public NccNetworkDeviceData fillData(){
        NccNetworkDeviceData networkDeviceData = new NccNetworkDeviceData();

        try {
            networkDeviceData.id = rs.getInt("id");
            networkDeviceData.deviceIP = rs.getLong("deviceIP");
            networkDeviceData.deviceName = rs.getString("deviceName");
            networkDeviceData.deviceType = rs.getInt("deviceType");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return networkDeviceData;
    }
}
