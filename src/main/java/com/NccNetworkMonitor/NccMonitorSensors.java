package com.NccNetworkMonitor;

import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;

import java.util.ArrayList;

/**
 * Created by root on 02.11.16.
 */
public class NccMonitorSensors {

    public ArrayList<NccMonitorSensorData> getSensors() {
        return new NccMonitorSensorData().getDataList("SELECT * FROM nccMonitorSensors");
    }

    public NccMonitorSensorData getSensors(Integer id) {
        return new NccMonitorSensorData().getData("SELECT * FROM nccMonitorSensors WHERE id=" + id);
    }

    public NccMonitorSensorData updateSensor(NccMonitorSensorData sensorData) {

        try {
            NccQuery query = new NccQuery();
            query.updateQuery("UPDATE nccMonitorSensors SET " +
                    "sensorName='" + sensorData.sensorName + "', " +
                    "sensorType=" + sensorData.sensorType + ", " +
                    "sensorSource=" + sensorData.sensorSource + ", " +
                    "sensorLongValue=" + sensorData.sensorLongValue + ", " +
                    "sensorIntValue=" + sensorData.sensorIntValue + ", " +
                    "sensorStringValue='" + sensorData.sensorStringValue + "', " +
                    "sensorDoubleValue=" + sensorData.sensorDoubleValue + ", " +
                    "sensorStatus=" + sensorData.sensorStatus + " " +
                    "WHERE id=" + sensorData.id);
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return sensorData;
    }
}
