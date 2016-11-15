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

    public NccMonitorTriggerData getData(){
        return this.triggerData;
    }

    public Integer getStatus(){
        return this.triggerData.triggerStatus;
    }

    public void setStatus(Integer status) {
        this.triggerData.triggerStatus = status;
        new NccMonitorTrigger().updateTrigger(this.triggerData);
    }

    public void fireEvent(Sensor sensor, String event) {
        System.out.println("Event '" + event + "' for sensor id=" + sensor.getData().id);
    }
}
