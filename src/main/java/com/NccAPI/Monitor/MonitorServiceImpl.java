package com.NccAPI.Monitor;

import com.NccAPI.NccAPI;
import com.NccNetworkMonitor.NccMonitorSensorData;
import com.NccNetworkMonitor.NccMonitorSensors;
import com.NccNetworkMonitor.NccMonitorTrigger;
import com.NccNetworkMonitor.NccMonitorTriggerData;

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

    public ApiMonitorTriggerData getMonitorActiveTriggers(
            String login,
            String key) {

        ApiMonitorTriggerData apiMonitorTriggerData = new ApiMonitorTriggerData();

        apiMonitorTriggerData.data = new ArrayList<>();
        apiMonitorTriggerData.status = 1;
        apiMonitorTriggerData.message = "Error";

        if (!new NccAPI().checkPermission(login, key, "GetMonitorActiveTriggers")) {
            apiMonitorTriggerData.message = "Permission denied";
            return apiMonitorTriggerData;
        }

        ArrayList<NccMonitorTriggerData> data = new NccMonitorTrigger().getActiveTriggers();

        if (data != null) {
            if (data.size() > 0) {
                apiMonitorTriggerData.data = data;
                apiMonitorTriggerData.status = 0;
                apiMonitorTriggerData.message = "success";
            }
        }

        return apiMonitorTriggerData;
    }

}
