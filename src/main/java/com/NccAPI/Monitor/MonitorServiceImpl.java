package com.NccAPI.Monitor;

import com.NccNetworkMonitor.NccMonitorSensorData;
import com.NccNetworkMonitor.NccMonitorSensors;

import java.util.ArrayList;

public class MonitorServiceImpl implements MonitorService {

    public ApiMonitorSensorData getMonitorSensorsByDeviceId(
            String login,
            String key,
            Integer deviceId) {

        ApiMonitorSensorData apiMonitorSensorData = new ApiMonitorSensorData();

        apiMonitorSensorData.data = new ArrayList<>();
        apiMonitorSensorData.status = 1;
        apiMonitorSensorData.message = "Error";

        ArrayList<NccMonitorSensorData> data = new NccMonitorSensors().getSensorsByDeviceId(deviceId);

        if (data != null) {
            if (data.size() > 0) {
                apiMonitorSensorData.data = data;
                apiMonitorSensorData.status = 0;
                apiMonitorSensorData.message = "success";
            }
        }

        return apiMonitorSensorData;
    }

    public ApiMonitorSensorData discoverMonitorSensors(
            String login,
            String key,
            Integer deviceId) {

        ApiMonitorSensorData apiMonitorSensorData = new ApiMonitorSensorData();

        apiMonitorSensorData.data = new ArrayList<>();
        apiMonitorSensorData.status = 1;
        apiMonitorSensorData.message = "Error";

        ArrayList<NccMonitorSensorData> data = new NccMonitorSensors().discoverSensors(deviceId);

        if (data != null) {
            if (data.size() > 0) {
                apiMonitorSensorData.data = data;
                apiMonitorSensorData.status = 0;
                apiMonitorSensorData.message = "success";
            }
        }

        return apiMonitorSensorData;
    }

}
