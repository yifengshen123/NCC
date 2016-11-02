package com.NccNetworkMonitor;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 02.11.16.
 */
public class NccMonitorSensorHistoryData extends NccAbstractData<NccMonitorSensorHistoryData> {
    public Integer id;
    public Integer sensorId;
    public Long historyTime;
    public Long sensorLongValue;
    public Integer sensorIntValue;
    public String sensorStringValue;
    public Double sensorDoubleValue;

    @Override
    public NccMonitorSensorHistoryData fillData(){
        NccMonitorSensorHistoryData data = new NccMonitorSensorHistoryData();

        try {
            data.id = rs.getInt("id");
            data.sensorId = rs.getInt("sensorId");
            data.historyTime = rs.getLong("historyTime");
            data.sensorLongValue = rs.getLong("sensorLongValue");
            data.sensorIntValue = rs.getInt("sensorIntValue");
            data.sensorStringValue = rs.getString("sensorStringValue");
            data.sensorDoubleValue = rs.getDouble("sensorDoubleValue");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}
