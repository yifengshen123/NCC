package com.NccNetworkMonitor;

import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;

/**
 * Created by root on 02.11.16.
 */
public class NccMonitorSensorHistory {

    public void add(NccMonitorSensorData sensorData) {
        try {
            NccQuery query = new NccQuery();

            query.updateQuery("INSERT INTO nccMonitorSensorHistory " +
                    "(sensorId, " +
                    "historyTime, " +
                    "sensorLongValue, " +
                    "sensorIntValue, " +
                    "sensorStringValue, " +
                    "sensorDoubleValue" +
                    ") VALUES (" +
                    sensorData.id + ", " +
                    "UNIX_TIMESTAMP(NOW()), " +
                    sensorData.sensorLongValue + ", " +
                    sensorData.sensorIntValue + ", " +
                    "'" + sensorData.sensorStringValue + "', " +
                    sensorData.sensorDoubleValue +
                    ")");
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }
}
