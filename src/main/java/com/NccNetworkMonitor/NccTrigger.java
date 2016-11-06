package com.NccNetworkMonitor;

import com.Ncc;
import com.NccSystem.NccLogger;
import org.apache.log4j.Logger;

/**
 * Created by root on 06.11.16.
 */
public class NccTrigger {
    private static NccLogger triggerLogger = new NccLogger("TriggerLogger");
    private static Logger logger = triggerLogger.setFilename(Ncc.netmonLogfile);

    public void setTrigger(NccMonitorTriggerData trigger, NccMonitorSensorData sensor) {
        logger.info("sensor[" + sensor.id + "][" + sensor.sensorLongValue + "] trigger[" + trigger.id + "][" + trigger.triggerStatus + "]");
    }
}
