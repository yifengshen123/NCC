package com.NccAPI.Monitor;

import com.NccAPI.NccAPI;
import com.NccNetworkMonitor.NccMonitorSensorData;
import com.NccNetworkMonitor.NccMonitorSensors;
import com.NccNetworkMonitor.NccMonitorTrigger;
import com.NccNetworkMonitor.NccMonitorTriggerData;

import java.util.ArrayList;

public class MonitorServiceImpl implements MonitorService {

    public ApiMonitorSensorData createMonitorSensor(
            String login,
            String key,
            String sensorName,
            Integer pollInterval,
            String sensorCode) {

        ApiMonitorSensorData apiMonitorSensorData = new ApiMonitorSensorData();

        apiMonitorSensorData.data = new ArrayList<>();
        apiMonitorSensorData.status = 1;
        apiMonitorSensorData.message = "Error";

        NccMonitorSensorData sensorData = new NccMonitorSensorData();
        sensorData.sensorName = sensorName;
        sensorData.pollInterval = pollInterval;
        sensorData.sensorCode = sensorCode;

        sensorData = new NccMonitorSensors().createSensor(sensorData);

        if (sensorData != null) {
            apiMonitorSensorData.data.add(sensorData);
            apiMonitorSensorData.status = 0;
            apiMonitorSensorData.message = "success";
        }

        return apiMonitorSensorData;
    }

    public ApiMonitorSensorData updateMonitorSensor(
            String login,
            String key,
            Integer id,
            String sensorName,
            Integer pollInterval,
            String sensorCode) {

        ApiMonitorSensorData apiMonitorSensorData = new ApiMonitorSensorData();

        apiMonitorSensorData.data = new ArrayList<>();
        apiMonitorSensorData.status = 1;
        apiMonitorSensorData.message = "Error";

        NccMonitorSensorData sensorData = new NccMonitorSensorData();
        sensorData.id = id;
        sensorData.sensorName = sensorName;
        sensorData.pollInterval = pollInterval;
        sensorData.sensorCode = sensorCode;

        sensorData = new NccMonitorSensors().updateSensor(sensorData);

        if (sensorData != null) {
            apiMonitorSensorData.data.add(sensorData);
            apiMonitorSensorData.status = 0;
            apiMonitorSensorData.message = "success";
        }

        return apiMonitorSensorData;
    }

    public ApiMonitorSensorData deleteMonitorSensor(
            String login,
            String key,
            Integer id) {

        ApiMonitorSensorData apiMonitorSensorData = new ApiMonitorSensorData();

        apiMonitorSensorData.data = new ArrayList<>();
        apiMonitorSensorData.status = 1;
        apiMonitorSensorData.message = "Error";

        NccMonitorSensorData sensorData = new NccMonitorSensors().deleteSensor(id);

        if (sensorData != null) {
            apiMonitorSensorData.data.add(sensorData);
            apiMonitorSensorData.status = 0;
            apiMonitorSensorData.message = "success";
        }

        return apiMonitorSensorData;
    }

    public ApiMonitorSensorData getMonitorSensors(
            String login,
            String key) {

        ApiMonitorSensorData apiMonitorSensorData = new ApiMonitorSensorData();

        apiMonitorSensorData.data = new ArrayList<>();
        apiMonitorSensorData.status = 1;
        apiMonitorSensorData.message = "Error";

        ArrayList<NccMonitorSensorData> data = new NccMonitorSensors().getSensors();

        if (data != null) {
            if (data.size() > 0) {
                apiMonitorSensorData.data = data;
                apiMonitorSensorData.status = 0;
                apiMonitorSensorData.message = "success";
            }
        }

        return apiMonitorSensorData;
    }

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
