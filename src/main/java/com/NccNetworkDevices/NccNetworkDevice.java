package com.NccNetworkDevices;

import com.Ncc;
import com.NccNetworkMonitor.SNMP.SnmpDiscover;
import com.NccSNMP.NccSNMP;
import com.NccSystem.NccLogger;
import com.NccSystem.NccUtils;
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
        return new NccNetworkDeviceData().getDataList("SELECT " +
                "d.id AS id, " +
                "d.deviceIP AS deviceIP, " +
                "d.deviceName AS deviceName, " +
                "d.deviceType AS deviceType, " +
                "d.snmpCommunity AS snmpCommunity, " +
                "d.addressStreet AS addressStreet, " +
                "d.addressBuild AS addressBuild, " +
                "t.typeName AS typeName " +
                "FROM nccNetworkDevices d " +
                "LEFT JOIN nccDeviceTypes t ON t.id=d.deviceType");
    }

    public NccNetworkDeviceData getNetworkDevices(Integer id) {
        return new NccNetworkDeviceData().getData("SELECT " +
                "d.id AS id, " +
                "d.deviceIP AS deviceIP, " +
                "d.deviceName AS deviceName, " +
                "d.deviceType AS deviceType, " +
                "d.snmpCommunity AS snmpCommunity, " +
                "d.addressStreet AS addressStreet, " +
                "d.addressBuild AS addressBuild, " +
                "t.typeName AS typeName " +
                "FROM nccNetworkDevices d " +
                "LEFT JOIN nccDeviceTypes t ON t.id=d.deviceType " +
                "WHERE d.id=" + id);
    }

    public ArrayList<IfaceData> getIfaces(Integer id) {
        return new IfaceData().getDataList("SELECT * FROM nccNetworkDeviceIfaces WHERE deviceId=" + id);
    }

    public ArrayList<IfaceData> updateIfaces(Integer id) {

        NccNetworkDeviceData deviceData = getNetworkDevices(id);

        ArrayList<IfaceData> ifaces = new SnmpDiscover(new NccSNMP(
                NccUtils.long2ip(deviceData.deviceIP),
                deviceData.snmpCommunity)).getIfaces();

        if (ifaces != null) {
            ArrayList<String> queries = new ArrayList<>();

            for (IfaceData iface : ifaces) {
                queries.add("INSERT INTO nccNetworkDeviceIfaces (" +
                        "deviceId, " +
                        "ifIndex, " +
                        "ifType, " +
                        "ifDescr, " +
                        "ifSpeed, " +
                        "ifOperStatus, " +
                        "ifAdminStatus, " +
                        "ifInOctets, " +
                        "ifOutOctets," +
                        "ifHCInOctets, " +
                        "ifHCOutOctets, " +
                        "ifInUcastPkts, " +
                        "ifOutUcastPkts, " +
                        "ifInErrors, " +
                        "ifOutErrors, " +
                        "ifInDiscards, " +
                        "ifOutDiscards, " +
                        "ifInNUcastPkts, " +
                        "ifOutNUcastPkts, " +
                        "ifPhysAddress, " +
                        "lastUpdate) VALUES (" +
                        id + ", " +
                        iface.ifIndex + ", " +
                        iface.ifType + ", " +
                        "'" + iface.ifDescr + "', " +
                        iface.ifSpeed + ", " +
                        iface.ifOperStatus + ", " +
                        iface.ifAdminStatus + ", " +
                        iface.ifInOctets + ", " +
                        iface.ifOutOctets + ", " +
                        iface.ifHCInOctets + ", " +
                        iface.ifHCOutOctets + ", " +
                        iface.ifInUcastPkts + ", " +
                        iface.ifOutUcastPkts + ", " +
                        iface.ifInErrors + ", " +
                        iface.ifOutErrors + ", " +
                        iface.ifInDiscards + ", " +
                        iface.ifOutDiscards + ", " +
                        iface.ifInNUcastPkts + ", " +
                        iface.ifOutNUcastPkts + ", " +
                        "'" + iface.ifPhysAddress + "', " +
                        "UNIX_TIMESTAMP(NOW())) " +
                        "ON DUPLICATE KEY " +
                        "UPDATE " +
                        "ifIndex=" + iface.ifIndex + ", " +
                        "ifType=" + iface.ifType + ", " +
                        "ifSpeed=" + iface.ifSpeed + ", " +
                        "ifOperStatus=" + iface.ifOperStatus + ", " +
                        "ifAdminStatus=" + iface.ifAdminStatus + ", " +
                        "ifInOctets=" + iface.ifInOctets + ", " +
                        "ifOutOctets=" + iface.ifOutOctets + ", " +
                        "ifHCInOctets=" + iface.ifHCInOctets + ", " +
                        "ifHCOutOctets=" + iface.ifHCOutOctets + ", " +
                        "ifInUcastPkts=" + iface.ifInUcastPkts + ", " +
                        "ifOutUcastPkts=" + iface.ifOutUcastPkts + ", " +
                        "ifInErrors=" + iface.ifInErrors + ", " +
                        "ifOutErrors=" + iface.ifOutErrors + ", " +
                        "ifInDiscards=" + iface.ifInDiscards + ", " +
                        "ifOutDiscards=" + iface.ifOutDiscards + ", " +
                        "ifInNUcastPkts=" + iface.ifInNUcastPkts + ", " +
                        "ifOutNUcastPkts=" + iface.ifOutNUcastPkts + ", " +
                        "ifPhysAddress='" + iface.ifPhysAddress + "', " +
                        "lastUpdate=UNIX_TIMESTAMP(NOW())");
            }

            try {
                NccQuery query = new NccQuery();
                query.updateBulkQuery(queries);
            } catch (NccQueryException e) {
                e.printStackTrace();
            }

        }

        return new NccNetworkDevice().getIfaces(id);
    }

    public ArrayList<IfaceData> discoverIfaces(Integer id) {

        NccNetworkDeviceData deviceData = getNetworkDevices(id);

        ArrayList<IfaceData> ifaces = new SnmpDiscover(new NccSNMP(
                NccUtils.long2ip(deviceData.deviceIP),
                deviceData.snmpCommunity)).getIfaces();

        if (ifaces != null) {
            ArrayList<String> queries = new ArrayList<>();

            for (IfaceData iface : ifaces) {
                queries.add("INSERT INTO nccNetworkDeviceIfaces (" +
                        "deviceId, " +
                        "ifIndex, " +
                        "ifType, " +
                        "ifDescr, " +
                        "ifSpeed, " +
                        "ifOperStatus, " +
                        "ifAdminStatus, " +
                        "ifInOctets, " +
                        "ifOutOctets," +
                        "ifHCInOctets, " +
                        "ifHCOutOctets, " +
                        "ifInUcastPkts, " +
                        "ifOutUcastPkts, " +
                        "ifInErrors, " +
                        "ifOutErrors, " +
                        "ifInDiscards, " +
                        "ifOutDiscards, " +
                        "ifInNUcastPkts, " +
                        "ifOutNUcastPkts, " +
                        "ifPhysAddress, " +
                        "lastUpdate) VALUES (" +
                        id + ", " +
                        iface.ifIndex + ", " +
                        iface.ifType + ", " +
                        "'" + iface.ifDescr + "', " +
                        iface.ifSpeed + ", " +
                        iface.ifOperStatus + ", " +
                        iface.ifAdminStatus + ", " +
                        iface.ifInOctets + ", " +
                        iface.ifOutOctets + ", " +
                        iface.ifHCInOctets + ", " +
                        iface.ifHCOutOctets + ", " +
                        iface.ifInUcastPkts + ", " +
                        iface.ifOutUcastPkts + ", " +
                        iface.ifInErrors + ", " +
                        iface.ifOutErrors + ", " +
                        iface.ifInDiscards + ", " +
                        iface.ifOutDiscards + ", " +
                        iface.ifInNUcastPkts + ", " +
                        iface.ifOutNUcastPkts + ", " +
                        "'" + iface.ifPhysAddress + "', " +
                        "UNIX_TIMESTAMP(NOW())) " +
                        "ON DUPLICATE KEY " +
                        "UPDATE " +
                        "ifIndex=" + iface.ifIndex + ", " +
                        "ifType=" + iface.ifType + ", " +
                        "ifDescr='" + iface.ifDescr + "', " +
                        "ifSpeed=" + iface.ifSpeed + ", " +
                        "ifOperStatus=" + iface.ifOperStatus + ", " +
                        "ifAdminStatus=" + iface.ifAdminStatus + ", " +
                        "ifInOctets=" + iface.ifInOctets + ", " +
                        "ifOutOctets=" + iface.ifOutOctets + ", " +
                        "ifHCInOctets=" + iface.ifHCInOctets + ", " +
                        "ifHCOutOctets=" + iface.ifHCOutOctets + ", " +
                        "ifInUcastPkts=" + iface.ifInUcastPkts + ", " +
                        "ifOutUcastPkts=" + iface.ifOutUcastPkts + ", " +
                        "ifInErrors=" + iface.ifInErrors + ", " +
                        "ifOutErrors=" + iface.ifOutErrors + ", " +
                        "ifInDiscards=" + iface.ifInDiscards + ", " +
                        "ifOutDiscards=" + iface.ifOutDiscards + ", " +
                        "ifInNUcastPkts=" + iface.ifInNUcastPkts + ", " +
                        "ifOutNUcastPkts=" + iface.ifOutNUcastPkts + ", " +
                        "ifPhysAddress='" + iface.ifPhysAddress + "', " +
                        "lastUpdate=UNIX_TIMESTAMP(NOW())");
            }

            try {
                NccQuery query = new NccQuery();
                query.updateBulkQuery(queries);
            } catch (NccQueryException e) {
                e.printStackTrace();
            }

        }

        return new NccNetworkDevice().getIfaces(id);
    }
}
