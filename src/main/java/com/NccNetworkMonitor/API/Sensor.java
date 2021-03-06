package com.NccNetworkMonitor.API;

import com.NccNetworkMonitor.NccMonitorSensorData;
import com.NccNetworkMonitor.NccMonitorSensorHistory;
import com.NccNetworkMonitor.NccMonitorSensors;

public class Sensor {

    public static final Integer SENSOR_PROCESSED = 50;

    public static final Integer SENSOR_LONG = 1;
    public static final Integer SENSOR_INT = 2;
    public static final Integer SENSOR_DOUBLE = 3;

    private NccMonitorSensorData sensorData;

    public Sensor() {
        this.sensorData = new NccMonitorSensorData();
    }

    public Sensor(Integer id) {
        this.sensorData = new NccMonitorSensors().getSensors(id);
    }

    public Sensor(String name) {
        get(name);
    }

    public Sensor get(Integer id) {
        Sensor sensor = new Sensor();
        sensor.sensorData = new NccMonitorSensors().getSensors(id);
        return sensor;
    }

    public Sensor get(String name) {
        Sensor sensor = new Sensor();
        sensor.sensorData = new NccMonitorSensors().getSensors(name);
        return sensor;
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
        new NccMonitorSensorHistory().add(getData());
    }

    public Integer getIntValue() {
        return this.sensorData.sensorIntValue;
    }

    public void setIntValue(Integer val) {
        this.sensorData.sensorIntValue = val;
        new NccMonitorSensors().updateSensor(getData());
        NccMonitorSensorData sensor = getData();
        new NccMonitorSensorHistory().add(sensor);
    }

    public Double getAvg(Integer type, Integer count) {
        return new NccMonitorSensorHistory().getAvg(getData().id, type, count);
    }

    public void setStatus(Integer status){
        this.sensorData.sensorStatus = status;
        new NccMonitorSensors().updateSensor(getData());
    }
}
