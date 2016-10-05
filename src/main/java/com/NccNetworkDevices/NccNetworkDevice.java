package com.NccNetworkDevices;

import com.Ncc;
import com.NccSystem.NccLogger;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import org.apache.log4j.Logger;

/**
 * Created by root on 05.10.16.
 */
public class NccNetworkDevice {

    private static NccLogger nccLogger = new NccLogger("NetworkDeviceLogger");
    private static Logger logger = nccLogger.setFilename(Ncc.logFile);
    private NccQuery query;

    public NccNetworkDevice(){
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

}
