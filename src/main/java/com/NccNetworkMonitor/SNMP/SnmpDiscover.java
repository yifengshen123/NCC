package com.NccNetworkMonitor.SNMP;

import com.NccNetworkDevices.IfaceData;
import com.NccSNMP.NccSNMP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 26.10.16.
 */
public class SnmpDiscover {

    private final String mibIfIndex = "1.3.6.1.2.1.2.2.1.1";
    private final String mibIfDescr = "1.3.6.1.2.1.2.2.1.2";
    private final String mibIfTypes = "1.3.6.1.2.1.2.2.1.3";
    private final String mibIfSpeed = "1.3.6.1.2.1.2.2.1.5";
    private final String mibIfPhysAddress = "1.3.6.1.2.1.2.2.1.6";
    private final String mibIfAdminStatus = "1.3.6.1.2.1.2.2.1.7";
    private final String mibIfOperStatus = "1.3.6.1.2.1.2.2.1.8";
    private final String mibIfInOctets = "1.3.6.1.2.1.2.2.1.10";
    private final String mibIfHCInOctets = "1.3.6.1.2.1.31.1.1.1.6";
    private final String mibIfInUcastPkts = "1.3.6.1.2.1.2.2.1.11";
    private final String mibIfInNUcastPkts = "1.3.6.1.2.1.2.2.1.12";
    private final String mibIfInDiscards = "1.3.6.1.2.1.2.2.1.13";
    private final String mibIfInErrors = "1.3.6.1.2.1.2.2.1.14";
    private final String mibIfOutOctets = "1.3.6.1.2.1.2.2.1.16";
    private final String mibIfHCOutOctets = "1.3.6.1.2.1.31.1.1.1.10";
    private final String mibIfOutUcastPkts = "1.3.6.1.2.1.2.2.1.17";
    private final String mibIfOutNUcastPkts = "1.3.6.1.2.1.2.2.1.18";
    private final String mibIfOutDiscards = "1.3.6.1.2.1.2.2.1.19";
    private final String mibIfOutErrors = "1.3.6.1.2.1.2.2.1.20";

    private HashMap<String, String> ifIndex;
    private HashMap<String, String> walkData;

    private NccSNMP snmpServer;

    private ArrayList<IfaceData> ifaces;

    public SnmpDiscover(NccSNMP server) {

        snmpServer = server;

        walkData = new HashMap<>();

        ifIndex = server.getStrings(mibIfIndex);
        if (ifIndex != null) {
            walkData.putAll(ifIndex);
            walkData.putAll(server.getStrings(mibIfDescr));
            walkData.putAll(server.getStrings(mibIfTypes));
            walkData.putAll(server.getStrings(mibIfSpeed));
            walkData.putAll(server.getStrings(mibIfAdminStatus));
            walkData.putAll(server.getStrings(mibIfOperStatus));
            walkData.putAll(server.getStrings(mibIfInOctets));
            walkData.putAll(server.getStrings(mibIfOutOctets));
            walkData.putAll(server.getStrings(mibIfPhysAddress));
            walkData.putAll(server.getStrings(mibIfInErrors));
            walkData.putAll(server.getStrings(mibIfOutErrors));
            walkData.putAll(server.getStrings(mibIfInUcastPkts));
            walkData.putAll(server.getStrings(mibIfOutUcastPkts));
            walkData.putAll(server.getStrings(mibIfInDiscards));
            walkData.putAll(server.getStrings(mibIfOutDiscards));
            walkData.putAll(server.getStrings(mibIfInNUcastPkts));
            walkData.putAll(server.getStrings(mibIfOutNUcastPkts));
            walkData.putAll(server.getStrings(mibIfHCInOctets));
            walkData.putAll(server.getStrings(mibIfHCOutOctets));
        }
    }

    public HashMap<String, String> getIfIndex() {
        return ifIndex;
    }

    private Integer getInteger(String oid, Integer idx) {
        for (Map.Entry<String, String> entry : walkData.entrySet()) {
            if (entry.getKey().contains(oid)) {
                String[] keys = entry.getKey().split("\\.");
                if (keys.length > 0) {
                    if (Integer.parseInt(keys[keys.length - 1]) == idx) {
                        return Integer.parseInt(entry.getValue());
                    }
                }
            }
        }
        return 0;
    }

    private Long getLong(String oid, Integer idx) {
        for (Map.Entry<String, String> entry : walkData.entrySet()) {
            if (entry.getKey().contains(oid)) {
                String[] keys = entry.getKey().split("\\.");
                if (keys.length > 0) {
                    if (Long.parseLong(keys[keys.length - 1]) == idx) {
                        return Long.parseLong(entry.getValue());
                    }
                }
            }
        }
        return 0L;
    }

    private String getString(String oid, Integer idx) {
        for (Map.Entry<String, String> entry : walkData.entrySet()) {
            if (entry.getKey().contains(oid)) {
                String[] keys = entry.getKey().split("\\.");
                if (keys.length > 0) {
                    if (Long.parseLong(keys[keys.length - 1]) == idx) {
                        return entry.getValue();
                    }
                }
            }
        }
        return "";
    }

    public Long getUptime() {
        return 0L;
    }

    public ArrayList<IfaceData> getIfaces() {
        ArrayList<IfaceData> ifaceData = new ArrayList<>();

        if (ifIndex != null) {
            for (Map.Entry<String, String> item : ifIndex.entrySet()) {
                IfaceData idata = new IfaceData();

                idata.ifIndex = Integer.parseInt(item.getValue());
                idata.ifType = getInteger(mibIfTypes, idata.ifIndex);
                idata.ifDescr = getString(mibIfDescr, idata.ifIndex);
                idata.ifSpeed = getLong(mibIfSpeed, idata.ifIndex);
                idata.ifPhysAddress = getString(mibIfPhysAddress, idata.ifIndex);
                idata.ifAdminStatus = getInteger(mibIfAdminStatus, idata.ifIndex);
                idata.ifOperStatus = getInteger(mibIfOperStatus, idata.ifIndex);
                idata.ifInOctets = getLong(mibIfInOctets, idata.ifIndex);
                idata.ifOutOctets = getLong(mibIfOutOctets, idata.ifIndex);
                idata.ifInErrors = getLong(mibIfInErrors, idata.ifIndex);
                idata.ifOutErrors = getLong(mibIfOutErrors, idata.ifIndex);
                idata.ifInDiscards = getLong(mibIfInDiscards, idata.ifIndex);
                idata.ifOutDiscards = getLong(mibIfOutDiscards, idata.ifIndex);
                idata.ifInUcastPkts = getLong(mibIfInUcastPkts, idata.ifIndex);
                idata.ifOutUcastPkts = getLong(mibIfOutUcastPkts, idata.ifIndex);
                idata.ifInNUcastPkts = getLong(mibIfInNUcastPkts, idata.ifIndex);
                idata.ifOutNUcastPkts = getLong(mibIfOutNUcastPkts, idata.ifIndex);
                idata.ifHCInOctets = getLong(mibIfHCInOctets, idata.ifIndex);
                idata.ifHCOutOctets = getLong(mibIfHCOutOctets, idata.ifIndex);

                ifaceData.add(idata);
            }
        }

        return ifaceData;
    }

    public void setIfaceState(Integer iface, Integer state) {
        snmpServer.setInt(mibIfAdminStatus + "." + iface, state);
    }
}
