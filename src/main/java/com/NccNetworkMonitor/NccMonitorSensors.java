package com.NccNetworkMonitor;

import com.NccNetworkDevices.IfaceData;
import com.NccNetworkDevices.NccNetworkDevice;
import com.NccNetworkDevices.NccNetworkDeviceData;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;

import java.util.ArrayList;

public class NccMonitorSensors {

    public ArrayList<NccMonitorSensorData> getSensorsByDeviceId(Integer id) {
        return new NccMonitorSensorData().getDataList("SELECT * FROM nccMonitorSensors WHERE deviceId=" + id);
    }

    public ArrayList<NccMonitorSensorData> getSensorsBySource(Integer id) {
        return new NccMonitorSensorData().getDataList("SELECT * FROM nccMonitorSensors WHERE sensorSource=" + id);
    }

    public ArrayList<NccMonitorSensorData> getSensors() {
        return new NccMonitorSensorData().getDataList("SELECT * FROM nccMonitorSensors");
    }

    public NccMonitorSensorData getSensors(Integer id) {
        return new NccMonitorSensorData().getData("SELECT * FROM nccMonitorSensors WHERE id=" + id);
    }

    public NccMonitorSensorData getSensors(String name) {
        return new NccMonitorSensorData().getData("SELECT * FROM nccMonitorSensors WHERE sensorName='" + name + "'");
    }

    public NccMonitorSensorData updateSensor(NccMonitorSensorData sensorData) {

        try {
            NccQuery query = new NccQuery();
            query.updateQuery("UPDATE nccMonitorSensors SET " +
                    "sensorName='" + sensorData.sensorName + "', " +
                    "sensorCode='" + sensorData.sensorCode + "', " +
                    "sensorLongValue=" + sensorData.sensorLongValue + ", " +
                    "sensorIntValue=" + sensorData.sensorIntValue + ", " +
                    "sensorStringValue='" + sensorData.sensorStringValue + "', " +
                    "sensorDoubleValue=" + sensorData.sensorDoubleValue + ", " +
                    "sensorStatus=" + sensorData.sensorStatus + "," +
                    "pollInterval=" + sensorData.pollInterval + ", " +
                    "lastUpdate=UNIX_TIMESTAMP(NOW()) " +
                    "WHERE id=" + sensorData.id);
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return sensorData;
    }

    public NccMonitorSensorData createSensor(NccMonitorSensorData sensorData) {
        NccMonitorSensorData data = new NccMonitorSensorData();

        try {
            NccQuery query = new NccQuery();
            query.updateQuery("INSERT INTO nccMonitorSensors (" +
                    "sensorName, " +
                    "sensorCode, " +
                    "sensorLongValue, " +
                    "sensorIntValue, " +
                    "sensorStringValue, " +
                    "sensorDoubleValue, " +
                    "sensorStatus, " +
                    "pollInterval, " +
                    "lastUpdate " +
                    ") VALUES (" +
                    "'" + sensorData.sensorName + "', " +
                    "'" + sensorData.sensorCode + "', " +
                    sensorData.sensorLongValue + ", " +
                    sensorData.sensorIntValue + ", " +
                    "'" + sensorData.sensorStringValue + "', " +
                    sensorData.sensorDoubleValue + ", " +
                    sensorData.sensorStatus + ", " +
                    sensorData.pollInterval + ", " +
                    "UNIX_TIMESTAMP(NOW())" +
                    ") ON DUPLICATE KEY UPDATE " +
                    "sensorName='" + sensorData.sensorName + "', " +
                    "sensorCode='" + sensorData.sensorCode + "', " +
                    "sensorLongValue=" + sensorData.sensorLongValue + ", " +
                    "sensorIntValue=" + sensorData.sensorIntValue + ", " +
                    "sensorStringValue='" + sensorData.sensorStringValue + "', " +
                    "sensorDoubleValue=" + sensorData.sensorDoubleValue + ", " +
                    "sensorStatus=" + sensorData.sensorStatus + "," +
                    "pollInterval=" + sensorData.pollInterval + ", " +
                    "lastUpdate=UNIX_TIMESTAMP(NOW())");

            return sensorData;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return data;
    }

    public NccMonitorSensorData deleteSensor(Integer id) {
        NccMonitorSensorData sensorData = getSensors(id);

        try {
            NccQuery query = new NccQuery();
            query.updateQuery("DELETE FROM nccMonitorSensors WHERE id=" + id);
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return sensorData;
    }

    public ArrayList<NccMonitorSensorData> discoverSensors(Integer deviceId) {
        ArrayList<NccMonitorSensorData> data = new ArrayList<>();
        NccNetworkDeviceData deviceData = new NccNetworkDevice().getNetworkDevices(deviceId);
        NccMonitorSensorData sensor = new NccMonitorSensorData();

        sensor.pollInterval = 5;
        sensor.sensorStatus = 0;
        sensor.sensorCode = "sensor.setIntValue(device.get(\"" + deviceData.deviceName + "\").isAlive())";
        sensor.sensorName = deviceData.id + "_" + deviceData.deviceName + "_alive";

        data.add(createSensor(sensor));

        for (IfaceData iface : new NccNetworkDevice().getIfaces(deviceId)) {

            sensor.pollInterval = 30;
            sensor.sensorStatus = 0;
            sensor.sensorCode = "sensor.setLongValue(device.get(\"" + deviceData.deviceName + "\").getHCInOctets(" + iface.ifIndex + "))";
            sensor.sensorName = deviceData.id + "_" + deviceData.deviceName + "_port_" + iface.ifIndex + "_in";

            data.add(createSensor(sensor));

            sensor.pollInterval = 30;
            sensor.sensorStatus = 0;
            sensor.sensorCode = "sensor.setLongValue(device.get(\"" + deviceData.deviceName + "\").getHCOutOctets(" + iface.ifIndex + "))";
            sensor.sensorName = deviceData.id + "_" + deviceData.deviceName + "_port_" + iface.ifIndex + "_out";

            data.add(createSensor(sensor));
        }

        return data;
    }
}
