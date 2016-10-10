package com.NccNetworkDevices;

import com.Ncc;
import com.NccSystem.NccLogger;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by root on 05.10.16.
 */
public class NccNetworkDevice {

    private static NccLogger nccLogger = new NccLogger("NetworkDeviceLogger");
    private static Logger logger = nccLogger.setFilename(Ncc.logFile);
    private NccQuery query;

    public NccNetworkDevice() {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<NccNetworkDeviceType> getDeviceTypes() {
        return new NccNetworkDeviceType().getDataList("SELECT * FROM nccDeviceTypes");
    }

    public void createDevice(NccNetworkDeviceData device) {
        try {
            new NccQuery().updateQuery("INSERT INTO nccNetworkDevices (" +
                    "deviceName, " +
                    "deviceType, " +
                    "deviceIP, " +
                    "snmpCommunity, " +
                    "addressStreet, " +
                    "addressBuild) VALUES (" +
                    "'" + device.deviceName + "', " +
                    device.deviceType + ", " +
                    device.deviceIP + ", " +
                    "'" + device.snmpCommunity + "', " +
                    "'" + device.addressStreet + "', " +
                    "'" + device.addressBuild + "')");
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public void updateDevice(NccNetworkDeviceData device) {
        try {
            new NccQuery().updateQuery("UPDATE nccNetworkDevices SET " +
                    "deviceName='" + device.deviceName + "', " +
                    "deviceType=" + device.deviceType + ", " +
                    "deviceIP=" + device.deviceIP + ", " +
                    "addressStreet='" + device.addressStreet + "', " +
                    "addressBuild='" + device.addressBuild + "', " +
                    "snmpCommunity='" + device.snmpCommunity + "' " +
                    "WHERE id=" + device.id);
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public void deleteDevice(Integer id) {
        try {
            new NccQuery().updateQuery("DELETE FROM nccNetworkDevices WHERE id=" + id);
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<NccNetworkDeviceData> getNetworkDevices() {
        return new NccNetworkDeviceData().getDataList("SELECT * FROM nccNetworkDevices");
    }
}
