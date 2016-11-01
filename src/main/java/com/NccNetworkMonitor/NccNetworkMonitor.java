package com.NccNetworkMonitor;

import com.Ncc;
import com.NccNetworkDevices.IfaceData;
import com.NccNetworkDevices.NccNetworkDevice;
import com.NccNetworkDevices.NccNetworkDeviceData;
import com.NccSystem.NccLogger;
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

    private MonitorTask monitorTask;
    private Timer monitorTimer;

    public NccNetworkMonitor() {
        monitorTask = new MonitorTask();
        monitorTimer = new Timer();
    }

    public void start() {
        logger.info("Starting NetworkMonitor");

        monitorTimer.schedule(monitorTask, 0, 1 * 10 * 1000);
    }

    public void stop() {
        monitorTimer.cancel();
    }
}
