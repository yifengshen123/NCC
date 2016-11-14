package com.NccNetworkMonitor;

import com.Ncc;
import com.NccNetworkDevices.IfaceData;
import com.NccNetworkDevices.NccNetworkDevice;
import com.NccNetworkDevices.NccNetworkDeviceData;
import com.NccNetworkMonitor.API.Device;
import com.NccNetworkMonitor.API.Sensor;
import com.NccNetworkMonitor.API.Trigger;
import com.NccSystem.NccLogger;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import org.apache.log4j.Logger;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

public class NccNetworkMonitor {

    private static NccLogger netmonLogger = new NccLogger("NetmonLogger");
    private static Logger logger = netmonLogger.setFilename(Ncc.netmonLogfile);

    private class MonitorTask extends TimerTask {

        private NccNetworkDevice networkDevice = new NccNetworkDevice();

        @Override
        public void run() {

            Long startTime = System.currentTimeMillis();

            ArrayList<NccNetworkDeviceData> devices = networkDevice.getNetworkDevices();

            if (devices != null) {
                ExecutorService e = Executors.newFixedThreadPool(devices.size());
                for (NccNetworkDeviceData data : devices) {
                    Future f = e.submit(new DevicePoller(data));
                }
                e.shutdown();
                try {
                    e.awaitTermination(3, TimeUnit.SECONDS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                logger.debug("Processed " + devices.size() + " devices in " + (System.currentTimeMillis() - startTime) + " ms");
            }

        }
    }

    private class TriggerTask extends TimerTask {
        @Override
        public void run() {
            ArrayList<NccMonitorTriggerData> triggers = new NccMonitorTrigger().getTriggers();
            PythonInterpreter pi = new PythonInterpreter();

            for (NccMonitorTriggerData trigger : triggers) {
                if (!trigger.triggerCode.isEmpty()) {
                    pi.set("device", new Device());
                    pi.set("sensor", new Sensor());
                    pi.set("trigger", new Trigger(trigger.id));
                    pi.exec(trigger.triggerCode);
                }
            }
        }
    }

    private class SensorsTask extends TimerTask {
        @Override
        public void run() {
            ArrayList<NccMonitorSensorData> sensors = new NccMonitorSensors().getSensors();
            Long startTime = System.currentTimeMillis();
            Long sensorCount = 0L;

            for (NccMonitorSensorData sensor : sensors) {
                Long sqlTime = System.currentTimeMillis() / 1000;

                try {
                    sqlTime = new NccQuery().getSQLTime();
                } catch (NccQueryException e1) {
                    e1.printStackTrace();
                }

                if ((sqlTime - sensor.lastUpdate) < sensor.pollInterval) continue;

                sensorCount++;
                if (!sensor.sensorCode.isEmpty()) {
                    PythonInterpreter pi = new PythonInterpreter();
                    pi.set("device", new Device());
                    pi.set("sensor", new Sensor(sensor.id));
                    pi.exec(sensor.sensorCode);
                }
            }

            if (sensorCount > 0)
                System.out.println("Processed " + sensorCount + " sensors in " + (System.currentTimeMillis() - startTime) + "ms");
        }

    }

    private SensorsTask sensorsTask;
    private TriggerTask triggerTask;
    private Timer sensorsTimer;
    private Timer triggerTimer;

    public NccNetworkMonitor() {
        sensorsTask = new SensorsTask();
        triggerTask = new TriggerTask();

        sensorsTimer = new Timer();
        triggerTimer = new Timer();
    }

    public void start() {
        logger.info("Starting NetworkMonitor");

        sensorsTimer.schedule(sensorsTask, 0, 1000);
        triggerTimer.schedule(triggerTask, 0, 1000);
    }

    public void stop() {
        sensorsTimer.cancel();
        triggerTimer.cancel();
    }
}
