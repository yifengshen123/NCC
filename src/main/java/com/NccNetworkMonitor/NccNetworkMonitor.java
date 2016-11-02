package com.NccNetworkMonitor;

import com.Ncc;
import com.NccNetworkDevices.IfaceData;
import com.NccNetworkDevices.NccNetworkDevice;
import com.NccNetworkDevices.NccNetworkDeviceData;
import com.NccSystem.NccLogger;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * Created by root on 26.10.16.
 */
public class NccNetworkMonitor {

    private static NccLogger netmonLogger = new NccLogger("NetmonLogger");
    private static Logger logger = netmonLogger.setFilename(Ncc.netmonLogfile);

    private ExecutorService e;

    private class DeviceMonitor extends Thread {

        private NccNetworkDeviceData data;

        public DeviceMonitor(NccNetworkDeviceData data) {
            this.data = data;
            this.setName("DeviceMonitor-" + data.id);
        }

        @Override
        public void run() {
            Long startTime = System.currentTimeMillis();

            ArrayList<IfaceData> ifaces = new NccNetworkDevice().updateIfaces(data.id);

            logger.info("Processed " + ifaces.size() + " interfaces on " + data.typeName + "(" + data.id + ") in " + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

    private class MonitorTask extends TimerTask {

        private NccNetworkDevice networkDevice = new NccNetworkDevice();

        @Override
        public void run() {


            Long startTime = System.currentTimeMillis();

            ArrayList<NccNetworkDeviceData> devices = networkDevice.getNetworkDevices();

            if (devices != null) {
                e = Executors.newFixedThreadPool(devices.size());
                for (NccNetworkDeviceData data : devices) {

/*
                    DeviceMonitor t = new DeviceMonitor(data);
                    t.start();
*/

                    Future f = e.submit(new DevicePoller(data));
                }
                e.shutdown();
                try {
                    e.awaitTermination(3, TimeUnit.SECONDS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            logger.info("Processed " + devices.size() + " devices in " + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

    private class SensorsTask extends TimerTask {
        @Override
        public void run() {
            for (NccMonitorSensorData sensor : new NccMonitorSensors().getSensors()) {
                IfaceData ifaceData;
                Long sqlTime = System.currentTimeMillis() / 1000;

                try {
                    sqlTime = new NccQuery().getSQLTime();
                } catch (NccQueryException e1) {
                    e1.printStackTrace();
                }

                if ((sqlTime - sensor.lastUpdate) < sensor.pollInterval) continue;

                switch (sensor.sensorType) {
                    case 1:
                        break;
                    case 2:
                        ifaceData = new NccNetworkDevice().getIface(sensor.sensorSource);
                        sensor.sensorLongValue = ifaceData.ifHCInOctets;
                        new NccMonitorSensorHistory().add(sensor);
                        new NccMonitorSensors().updateSensor(sensor);
                        logger.info("Sensor id=" + sensor.id + " updated with val=" + sensor.sensorLongValue);
                        break;
                    case 3:
                        ifaceData = new NccNetworkDevice().getIface(sensor.sensorSource);
                        sensor.sensorLongValue = ifaceData.ifHCOutOctets;
                        new NccMonitorSensorHistory().add(sensor);
                        new NccMonitorSensors().updateSensor(sensor);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private MonitorTask monitorTask;
    private SensorsTask sensorsTask;
    private Timer monitorTimer;
    private Timer sensorsTimer;

    public NccNetworkMonitor() {
        monitorTask = new MonitorTask();
        sensorsTask = new SensorsTask();
        monitorTimer = new Timer();
        sensorsTimer = new Timer();
    }

    public void start() {
        logger.info("Starting NetworkMonitor");

        monitorTimer.schedule(monitorTask, 0, 1 * 10 * 1000);
        sensorsTimer.schedule(sensorsTask, 0, 1000);
    }

    public void stop() {
        monitorTimer.cancel();
    }
}
