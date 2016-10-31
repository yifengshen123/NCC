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

/**
 * Created by root on 26.10.16.
 */
public class NccNetworkMonitor {

    private static NccLogger netmonLogger = new NccLogger("NetmonLogger");
    private static Logger logger = netmonLogger.setFilename(Ncc.netmonLogfile);

    private class MonitorTask extends TimerTask {
        @Override
        public void run() {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    Long t = System.currentTimeMillis();

                    ArrayList<NccNetworkDeviceData> devices = new NccNetworkDevice().getNetworkDevices();

                    logger.info("Processing " + devices.size() + " network devices");

                    if (devices != null) {
                        for (NccNetworkDeviceData data : devices) {

                            ArrayList<IfaceData> ifaces = new NccNetworkDevice().updateIfaces(data.id);

                            logger.info("Processed " + ifaces.size() + " interfaces on " + data.typeName + "(" + data.id + ")");
                        }
                    }

                    logger.info("Finished in " + (System.currentTimeMillis() - t) + " ms");
                }
            });

            t.run();
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
        monitorTimer.schedule(monitorTask, 0, 1 * 60 * 1000);
    }

    public void stop() {
        monitorTimer.cancel();
    }
}
