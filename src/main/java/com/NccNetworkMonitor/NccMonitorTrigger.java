package com.NccNetworkMonitor;

import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;

import java.util.ArrayList;

/**
 * Created by root on 05.11.16.
 */
public class NccMonitorTrigger {

    public ArrayList<NccMonitorTriggerData> getTriggers() {
        return new NccMonitorTriggerData().getDataList("SELECT * FROM nccMonitorTriggers");
    }

    public ArrayList<NccMonitorTriggerData> getTriggersBySensor(Integer id) {
        return new NccMonitorTriggerData().getDataList("SELECT * FROM nccMonitorTriggers WHERE triggerSensor=" + id);
    }

    public NccMonitorTriggerData getTrigger(Integer id) {
        return new NccMonitorTriggerData().getData("SELECT * FROM nccMonitorTriggers WHERE id=" + id);
    }

    public NccMonitorTriggerData getTrigger(String name) {
        return new NccMonitorTriggerData().getData("SELECT * FROM nccMonitorTriggers WHERE triggerName='" + name + "'");
    }

    public ArrayList<NccMonitorTriggerData> getActiveTriggers() {
        return new NccMonitorTriggerData().getDataList("SELECT * FROM nccMonitorTriggers WHERE triggerStatus=1");
    }

    public NccMonitorTriggerData updateTrigger(NccMonitorTriggerData triggerData) {
        try {
            NccQuery query = new NccQuery();
            query.updateQuery("UPDATE nccMonitorTriggers SET " +
                    "triggerName='" + triggerData.triggerName + "', " +
                    "triggerCode='" + triggerData.triggerCode + "', " +
                    "triggerSensor=" + triggerData.triggerSensor + ", " +
                    "triggerStatus=" + triggerData.triggerStatus + ", " +
                    "pollInterval=" + triggerData.pollInterval + ", " +
                    "lastUpdate=UNIX_TIMESTAMP(NOW()) " +
                    "WHERE id=" + triggerData.id);

        } catch (NccQueryException e) {
            e.printStackTrace();
        }
        return new NccMonitorTrigger().getTrigger(triggerData.id);
    }
}
