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

    private class TriggerTask implements Runnable {
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

    private class SensorsTask implements Runnable {
        @Override
        public void run() {

            class SensorPoller implements Callable<ArrayList<NccMonitorSensorData>> {

                private ArrayList<NccMonitorSensorData> sensors;

                public SensorPoller(ArrayList<NccMonitorSensorData> sensors) {
                    this.sensors = sensors;
                }

                @Override
                public ArrayList<NccMonitorSensorData> call() {

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

                        if (!sensor.sensorCode.isEmpty()) {
                            PythonInterpreter pi = new PythonInterpreter();
                            pi.set("device", new Device());
                            pi.set("sensor", new Sensor(sensor.id));
                            pi.set("trigger", new Trigger());
                            pi.exec(sensor.sensorCode);
                            pi.cleanup();
                        }

                        sensorCount++;
                    }

                    if (sensorCount > 0)
                        logger.info("Processed " + sensorCount + " sensors in " + (System.currentTimeMillis() - startTime) + "ms");

                    return sensors;
                }
            }

            ArrayList<NccMonitorSensorData> sensors = new NccMonitorSensors().getSensors();

            ExecutorService es = Executors.newCachedThreadPool();
            es.submit(new SensorPoller(sensors));
            es.shutdown();
            try {
                es.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private ScheduledExecutorService sensorService;
    private ScheduledExecutorService triggerService;

    public NccNetworkMonitor() {
        sensorService = Executors.newScheduledThreadPool(10);
        triggerService = Executors.newScheduledThreadPool(5);
    }

    public void start() {
        logger.info("Starting NetworkMonitor");

        sensorService.scheduleAtFixedRate(new SensorsTask(), 0, 1, TimeUnit.SECONDS);

        triggerService.scheduleAtFixedRate(new TriggerTask(), 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        triggerService.shutdown();
        sensorService.shutdown();
    }
}
