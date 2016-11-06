package com.NccNetworkDevices;

import com.Ncc;
import com.NccNetworkMonitor.SNMP.SnmpDiscover;
import com.NccSNMP.NccSNMP;
import com.NccSystem.NccLogger;
import com.NccSystem.NccUtils;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class NccNetworkDevice {

    private static NccLogger nccLogger = new NccLogger("NetworkDeviceLogger");
    private static Logger logger = nccLogger.setFilename(Ncc.logFile);

    public NccNetworkDevice() {
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
                "t.typeName AS typeName," +
                "d.deviceStatus AS deviceStatus, " +
                "d.lastUpdate AS lastUpdate " +
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
                "t.typeName AS typeName, " +
                "d.deviceStatus AS deviceStatus, " +
                "d.lastUpdate AS lastUpdate  " +
                "FROM nccNetworkDevices d " +
                "LEFT JOIN nccDeviceTypes t ON t.id=d.deviceType " +
                "WHERE d.id=" + id);
    }

    public IfaceData getIface(Integer id) {
        return new IfaceData().getData("SELECT * FROM nccNetworkDeviceIfaces WHERE id=" + id);
    }

    public ArrayList<IfaceData> getIfaces(Integer id) {
        return new IfaceData().getDataList("SELECT * FROM nccNetworkDeviceIfaces WHERE deviceId=" + id);
    }

    public ArrayList<IfaceData> getIfaces(Integer id, ArrayList<Integer> types) {
        String typesCondition = StringUtils.join(types, ", ");

        return new IfaceData().getDataList("SELECT * FROM nccNetworkDeviceIfaces WHERE " +
                "deviceId=" + id + " AND ifType IN (" + typesCondition + ")");
    }

    public String getUptime(Integer id) {
        NccNetworkDeviceData deviceData = getNetworkDevices(id);

        if (deviceData != null) {
            return new NccSNMP(
                    NccUtils.long2ip(deviceData.deviceIP),
                    deviceData.snmpCommunity).getString("1.3.6.1.2.1.1.3.0");
        }

        return null;
    }

    public void setDeviceStatus(Integer id, Integer status) {
        try {
            NccQuery query = new NccQuery();
            query.updateQuery("UPDATE nccNetworkDevices SET " +
                    "lastUpdate=UNIX_TIMESTAMP(NOW()), " +
                    "deviceStatus=" + status + " " +
                    "WHERE id=" + id);
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<IfaceData> updateIfaces(Integer id) {

        NccNetworkDeviceData deviceData = getNetworkDevices(id);

        String uptime = getUptime(id);

        if (uptime == null) {
            setDeviceStatus(id, 0);
            return null;
        } else {
            setDeviceStatus(id, 1);
        }

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
