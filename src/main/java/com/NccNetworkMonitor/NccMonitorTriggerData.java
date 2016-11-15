package com.NccNetworkMonitor;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 05.11.16.
 */
public class NccMonitorTriggerData extends NccAbstractData<NccMonitorTriggerData> {
    public Integer id;
    public String triggerName;
    public String triggerCode;
    public Integer triggerSensor;
    public Integer triggerStatus;
    public Integer pollInterval;
    public Long lastUpdate;

    @Override
    public NccMonitorTriggerData fillData() {
        NccMonitorTriggerData data = new NccMonitorTriggerData();

        try {
            data.id = rs.getInt("id");
            data.triggerName = rs.getString("triggerName");
            data.triggerCode = rs.getString("triggerCode");
            data.triggerSensor = rs.getInt("triggerSensor");
            data.triggerStatus = rs.getInt("triggerStatus");
            data.pollInterval = rs.getInt("pollInterval");
            data.lastUpdate = rs.getLong("lastUpdate");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}
