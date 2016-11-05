package com.NccNetworkMonitor;

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
}
