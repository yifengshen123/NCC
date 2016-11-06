package com.NccAPI.Monitor;

public interface MonitorService {

    ApiMonitorSensorData getMonitorSensorsByDeviceId(
            String login,
            String key,
            Integer deviceId);

    ApiMonitorSensorData discoverMonitorSensors(
            String login,
            String key,
            Integer deviceId);
}
