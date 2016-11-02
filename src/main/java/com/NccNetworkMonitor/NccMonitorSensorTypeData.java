package com.NccNetworkMonitor;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 02.11.16.
 */
public class NccMonitorSensorTypeData extends NccAbstractData<NccMonitorSensorTypeData> {
    public Integer id;
    public String typeName;

    @Override
    public NccMonitorSensorTypeData fillData() {
        NccMonitorSensorTypeData data = new NccMonitorSensorTypeData();

        try {
            data.id = rs.getInt("id");
            data.typeName = rs.getString("typeName");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}
