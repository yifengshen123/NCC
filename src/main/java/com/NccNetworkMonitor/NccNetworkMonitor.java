package com.NccNetworkMonitor;

import com.Ncc;
import com.NccNetworkDevices.IfaceData;
import com.NccNetworkDevices.NccNetworkDevice;
import com.NccNetworkDevices.NccNetworkDeviceData;
import com.NccSystem.NccLogger;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import org.apache.log4j.Logger;
import org.python.core.PyInteger;
import org.python.util.PythonInterpreter;

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

    private void testTrigger(NccMonitorSensorData sensor){
        PythonInterpreter pi = new PythonInterpreter();
        ArrayList<NccMonitorTriggerData> triggers = new NccMonitorTrigger().getTriggersBySensor(sensor.id);

        for(NccMonitorTriggerData triggerData: triggers){
            pi.set("sensor", sensor);
            pi.exec(triggerData.triggerCode);
            PyInteger result = (PyInteger) pi.get("result");
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
                        logger.info("Sensor type=" + sensor.sensorType + " id=" + sensor.id + " updated with val=" + sensor.sensorLongValue);
                        break;
                    case 3:
                        ifaceData = new NccNetworkDevice().getIface(sensor.sensorSource);
                        sensor.sensorLongValue = ifaceData.ifHCOutOctets;
                        new NccMonitorSensorHistory().add(sensor);
                        new NccMonitorSensors().updateSensor(sensor);
                        logger.info("Sensor type=" + sensor.sensorType + " id=" + sensor.id + " updated with val=" + sensor.sensorLongValue);
                        break;
                    default:
                        break;
                }

                testTrigger(sensor);
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

    private void setTrigger(int val) {
        System.out.println("set trigger val=" + val);
    }

    private void test() {
        PythonInterpreter pythonInterpreter = new PythonInterpreter();

        pythonInterpreter.set("val", 3);
        pythonInterpreter.exec("" +
                "if val>0:\n" +
                "   result=1\n" +
                "else:\n" +
                "   result=5\n" +
                "");

        PyInteger result = (PyInteger) pythonInterpreter.get("result");

        System.out.println(result.toString());
    }

    public void stop() {
        monitorTimer.cancel();
    }
}
