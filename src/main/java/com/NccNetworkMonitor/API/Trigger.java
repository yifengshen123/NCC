package com.NccNetworkMonitor.API;

import com.NccNetworkMonitor.NccMonitorTrigger;
import com.NccNetworkMonitor.NccMonitorTriggerData;

public class Trigger {

    private NccMonitorTriggerData triggerData;

    public Trigger() {
        this.triggerData = new NccMonitorTriggerData();
    }

    public Trigger(Integer id) {
        get(id);
    }

    public Trigger(String name) {
        get(name);
    }

    public Trigger get(Integer id) {
        this.triggerData = new NccMonitorTrigger().getTrigger(id);
        return this;
    }

    public Trigger get(String name) {
        this.triggerData = new NccMonitorTrigger().getTrigger(name);
        return this;
    }

    public void setStatus(Integer status) {
        this.triggerData.triggerStatus = status;
        new NccMonitorTrigger().updateTrigger(this.triggerData);
    }
}
