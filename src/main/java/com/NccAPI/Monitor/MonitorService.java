package com.NccAPI.Monitor;

public interface MonitorService {
    ApiMonitorSensorData deleteMonitorSensor(
            String login,
            String key,
            Integer id);

    ApiMonitorSensorData updateMonitorSensor(
            String login,
            String key,
            Integer id,
            String sensorName,
            Integer pollInterval,
            String sensorCode);

    ApiMonitorSensorData createMonitorSensor(
            String login,
            String key,
            String sensorName,
            Integer pollInterval,
            String sensorCode);

    ApiMonitorSensorData getMonitorSensors(
            String login,
            String key);

    ApiMonitorSensorData getMonitorSensorsByDeviceId(
            String login,
            String key,
            Integer deviceId);

    ApiMonitorSensorData discoverMonitorSensors(
            String login,
            String key,
            Integer deviceId);

    ApiMonitorTriggerData getMonitorActiveTriggers(
            String login,
            String key);
}
