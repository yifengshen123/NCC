package com.NccNetworkMonitor.API;

import com.NccNetworkDevices.NccNetworkDevice;
import com.NccNetworkDevices.NccNetworkDeviceData;
import com.NccSNMP.NccSNMP;
import com.NccSystem.NccUtils;

import java.net.InetAddress;

public class Device {

    private NccNetworkDeviceData deviceData;

    public Device() {
        this.deviceData = new NccNetworkDeviceData();
    }

    public Device(Integer id) {
        get(id);
    }

    public Device get(Integer id) {
        this.deviceData = new NccNetworkDevice().getNetworkDevices(id);
        return this;
    }

    public Device get(String name) {
        this.deviceData = new NccNetworkDevice().getNetworkDevices(name);
        return this;
    }

    private boolean isReachable() {
        try {
            InetAddress ia = InetAddress.getByName(NccUtils.long2ip(this.deviceData.deviceIP));
            return ia.isReachable(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer isAlive() {
        if (isReachable()) return 1;
        else return 0;
    }

    public Long getHCInOctets(Integer iface) {
        NccSNMP snmp = new NccSNMP(NccUtils.long2ip(this.deviceData.deviceIP), this.deviceData.snmpCommunity);
        String val = snmp.getString(NccSNMP.ifHCInOctets + "." + iface.toString());
        return val.isEmpty() ? 0L : Long.parseLong(val);
    }

    public Long getHCOutOctets(Integer iface) {
        NccSNMP snmp = new NccSNMP(NccUtils.long2ip(this.deviceData.deviceIP), this.deviceData.snmpCommunity);
        String val = snmp.getString(NccSNMP.ifHCOutOctets + "." + iface.toString());
        return val.isEmpty() ? 0L : Long.parseLong(val);
    }

    public Integer getOperStatus(Integer iface) {
        NccSNMP snmp = new NccSNMP(NccUtils.long2ip(this.deviceData.deviceIP), this.deviceData.snmpCommunity);
        String val = snmp.getString(NccSNMP.ifOperStatus + "." + iface.toString());
        return val.isEmpty() ? 0 : Integer.parseInt(val);
    }
}
