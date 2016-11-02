package com.NccNetworkMonitor;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 02.11.16.
 */
public class NccMonitorSensorData extends NccAbstractData<NccMonitorSensorData> {
    public Integer id;
    public String sensorName;
    public Integer sensorType;
    public Integer sensorSource;
    public Long sensorLongValue;
    public Integer sensorIntValue;
    public String sensorStringValue;
    public Double sensorDoubleValue;
    public Integer sensorStatus;
    public Integer pollInterval;
    public Long lastUpdate;

    @Override
    public NccMonitorSensorData fillData() {
        NccMonitorSensorData data = new NccMonitorSensorData();

        try {
            data.id = rs.getInt("id");
            data.sensorName = rs.getString("sensorName");
            data.sensorType = rs.getInt("sensorType");
            data.sensorSource = rs.getInt("sensorSource");
            data.sensorLongValue = rs.getLong("sensorLongValue");
            data.sensorIntValue = rs.getInt("sensorIntValue");
            data.sensorStringValue = rs.getString("sensorStringValue");
            data.sensorDoubleValue = rs.getDouble("sensorDoubleValue");
            data.sensorStatus = rs.getInt("sensorStatus");
            data.pollInterval = rs.getInt("pollInterval");
            data.lastUpdate = rs.getLong("lastUpdate");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}
