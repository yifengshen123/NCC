package com.NccNetworkMonitor.API;

import com.NccNetworkMonitor.NccMonitorSensorData;
import com.NccNetworkMonitor.NccMonitorSensors;

public class Sensor {

    private NccMonitorSensorData sensorData;

    public Sensor() {
        this.sensorData = new NccMonitorSensorData();
    }

    public Sensor(Integer id) {
        get(id);
    }

    public Sensor(String name) {
        get(name);
    }

    public Sensor get(Integer id) {
        this.sensorData = new NccMonitorSensors().getSensors(id);
        return this;
    }

    public Sensor get(String name) {
        this.sensorData = new NccMonitorSensors().getSensors(name);
        return this;
    }

    public NccMonitorSensorData getData() {
        return this.sensorData;
    }

    public Long getLongValue() {
        return this.sensorData.sensorLongValue;
    }

    public void setLongValue(Long val) {
        this.sensorData.sensorLongValue = val;
        new NccMonitorSensors().updateSensor(getData());
    }

    public Integer getIntValue() {
        return this.sensorData.sensorIntValue;
    }

    public void setIntValue(Integer val) {
        this.sensorData.sensorIntValue = val;
        new NccMonitorSensors().updateSensor(getData());
    }
}
