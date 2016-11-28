package com.NccNetworkMonitor;

import com.Ncc;
import com.NccNetworkDevices.IfaceData;
import com.NccNetworkDevices.NccNetworkDevice;
import com.NccNetworkDevices.NccNetworkDeviceData;
import com.NccSystem.NccLogger;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by root on 01.11.16.
 */
public class DevicePoller implements Callable<ArrayList<IfaceData>> {

    private static NccLogger devicePollerLogger = new NccLogger("DevicePollerLogger");
    private static Logger logger = devicePollerLogger.setFilename(Ncc.netmonLogfile);

    private NccNetworkDeviceData data;
    private NccNetworkDevice nccNetworkDevice = new NccNetworkDevice();

    public DevicePoller(NccNetworkDeviceData data) {
        this.data = data;
    }

    @Override
    public ArrayList<IfaceData> call() {
        Long startTime = System.currentTimeMillis();

        ArrayList<IfaceData> ifaces = new ArrayList<>();
        ifaces = nccNetworkDevice.updateIfaces(data.id);

        logger.debug("Processed " + ifaces.size() + " interfaces on " + data.typeName + "(" + data.id + ") in " + (System.currentTimeMillis() - startTime) + " ms");
        return ifaces;
    }
}
