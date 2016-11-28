package com.NccNetworkMonitor;

import com.NccNetworkMonitor.API.Sensor;
import com.NccSystem.SQL.NccCachedRowset;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;

import java.sql.SQLException;

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

    public Double getAvg(Integer id, Integer type, Integer count) {
        try {
            NccQuery query = new NccQuery();
            NccCachedRowset rs = query.selectQuery("SELECT " +
                    "AVG(sensorLongValue) AS longAvg, " +
                    "AVG(sensorIntValue) AS intAvg, " +
                    "AVG(sensorDoubleValue) AS doubleAvg " +
                    "FROM nccMonitorSensorHistory " +
                    "WHERE sensorId=" + id + " " +
                    "ORDER BY historyTime DESC " +
                    "LIMIT " + count);
            if (rs != null) {
                try {
                    if (rs.next()) {
                        Double result = 0D;
                        if (type.equals(Sensor.SENSOR_LONG)) result = rs.getDouble("longAvg");
                        if (type.equals(Sensor.SENSOR_INT)) result = rs.getDouble("intAvg");
                        if (type.equals(Sensor.SENSOR_DOUBLE)) result = rs.getDouble("doubleAvg");
                        return result;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
        return 0D;
    }
}
