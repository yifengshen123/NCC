package com.NccAPI.NetworkDevices;

import com.NccAPI.NccAPI;
import com.NccNetworkDevices.IfaceData;
import com.NccNetworkDevices.NccNetworkDevice;
import com.NccNetworkDevices.NccNetworkDeviceData;
import com.NccNetworkDevices.NccNetworkDeviceType;
import com.NccSNMP.NccSNMP;
import com.NccSystem.NccUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class NetworkDevicesServiceImpl implements NetworkDevicesService {
    public ApiNetworkDeviceTypeData getNetworkDeviceTypes(String login, String key) {

        ApiNetworkDeviceTypeData typeData = new ApiNetworkDeviceTypeData();
        typeData.data = new ArrayList<NccNetworkDeviceType>();
        typeData.status = 1;
        typeData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetNetworkDeviceTypes")) {
            typeData.message = "Permission denied";
            return typeData;
        }

        ArrayList<NccNetworkDeviceType> types = new NccNetworkDevice().getDeviceTypes();

        if (types != null) {
            if (types.size() > 0) {
                typeData.data = types;
                typeData.status = 0;
                typeData.message = "success";
            }
        }

        return typeData;
    }

    public ApiNetworkDeviceData createNetworkDevice(String login, String key,
                                                    String deviceName,
                                                    String deviceIP,
                                                    Integer deviceType,
                                                    String snmpCommunity,
                                                    String addressStreet,
                                                    String addressBuild) {

        ApiNetworkDeviceData deviceData = new ApiNetworkDeviceData();
        deviceData.data = new ArrayList<NccNetworkDeviceData>();
        deviceData.status = 1;
        deviceData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "CreateNetworkDevice")) {
            deviceData.message = "Permission denied";
            return deviceData;
        }

        NccNetworkDeviceData device = new NccNetworkDeviceData();
        device.deviceName = deviceName;
        device.deviceType = deviceType;
        device.deviceIP = NccUtils.ip2long(deviceIP);
        device.snmpCommunity = snmpCommunity;
        device.addressStreet = addressStreet;
        device.addressBuild = addressBuild;

        if (device.deviceName.isEmpty()) {
            deviceData.message = "DeviceName is empty";
            return deviceData;
        }

        if (device.deviceType <= 0) {
            deviceData.message = "DeviceType must be >0";
            return deviceData;
        }

        if (device.deviceIP == null) {
            deviceData.message = "Incorrect DeviceIP";
            return deviceData;
        }

        new NccNetworkDevice().createDevice(device);

        deviceData.status = 0;
        deviceData.message = "success";
        return deviceData;
    }

    public ApiNetworkDeviceData getNetworkDevices(String login, String key) {

        ApiNetworkDeviceData deviceData = new ApiNetworkDeviceData();
        deviceData.data = new ArrayList<NccNetworkDeviceData>();
        deviceData.status = 1;
        deviceData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetNetworkDevices")) {
            deviceData.message = "Permission denied";
            return deviceData;
        }
        ;

        ArrayList<NccNetworkDeviceData> data = new NccNetworkDevice().getNetworkDevices();

        if (data != null) {
            if (data.size() > 0) {
                deviceData.data = data;
                deviceData.status = 0;
                deviceData.message = "success";
            }
        }

        return deviceData;
    }

    public ApiNetworkDeviceData updateNetworkDevice(String login, String key,
                                                    Integer id,
                                                    String deviceName,
                                                    String deviceIP,
                                                    Integer deviceType,
                                                    String snmpCommunity,
                                                    String addressStreet,
                                                    String addressBuild) {

        ApiNetworkDeviceData deviceData = new ApiNetworkDeviceData();
        deviceData.data = new ArrayList<NccNetworkDeviceData>();
        deviceData.status = 1;
        deviceData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "UpdateNetworkDevice")) {
            deviceData.message = "Permission denied";
            return deviceData;
        }

        NccNetworkDeviceData device = new NccNetworkDeviceData();
        device.id = id;
        device.deviceName = deviceName;
        device.deviceType = deviceType;
        device.deviceIP = NccUtils.ip2long(deviceIP);
        device.snmpCommunity = snmpCommunity;
        device.addressStreet = addressStreet;
        device.addressBuild = addressBuild;

        if (device.deviceName.isEmpty()) {
            deviceData.message = "DeviceName is empty";
            return deviceData;
        }

        if (device.deviceType <= 0) {
            deviceData.message = "DeviceType must be >0";
            return deviceData;
        }

        if (device.deviceIP == null) {
            deviceData.message = "Incorrect DeviceIP";
            return deviceData;
        }

        new NccNetworkDevice().updateDevice(device);

        deviceData.status = 0;
        deviceData.message = "success";
        return deviceData;
    }


    public ApiNetworkDeviceData deleteNetworkDevice(String login, String key, Integer id) {
        ApiNetworkDeviceData deviceData = new ApiNetworkDeviceData();

        deviceData.data = new ArrayList<NccNetworkDeviceData>();
        deviceData.status = 1;
        deviceData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "DeleteNetworkDevice")) {
            deviceData.message = "Permission denied";
            return deviceData;
        }

        if (id <= 0) {
            deviceData.message = "id must be >0";
            return deviceData;
        }

        new NccNetworkDevice().deleteDevice(id);
        deviceData.status = 0;
        deviceData.message = "success";
        return deviceData;
    }

    public ApiSnmpString getNetworkDeviceSnmpValue(String login, String key, Integer id, String oid) {
        ApiSnmpString snmpString = new ApiSnmpString();
        snmpString.data = "";
        snmpString.status = 1;
        snmpString.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetNetworkDeviceSnmpValue")) {
            snmpString.message = "Permission denied";
            return snmpString;
        }

        NccNetworkDeviceData device = new NccNetworkDevice().getNetworkDevices(id);

        if (device != null) {
            NccSNMP snmp = new NccSNMP(NccUtils.long2ip(device.deviceIP), device.snmpCommunity);
            snmpString.data = snmp.getString(oid);
            snmpString.status = 0;
            snmpString.message = "success";
        }
        return snmpString;
    }

    public ApiSnmpStrings getNetworkDeviceSnmpValues(String login, String key, Integer id, String oid) {
        ApiSnmpStrings snmpStrings = new ApiSnmpStrings();
        snmpStrings.data = new HashMap<String, String>();
        snmpStrings.status = 1;
        snmpStrings.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetNetworkDeviceSnmpValue")) {
            snmpStrings.message = "Permission denied";
            return snmpStrings;
        }

        NccNetworkDeviceData device = new NccNetworkDevice().getNetworkDevices(id);

        if (device != null) {
            NccSNMP snmp = new NccSNMP(NccUtils.long2ip(device.deviceIP), device.snmpCommunity);
            snmpStrings.data = snmp.getStrings(oid);
            snmpStrings.status = 0;
            snmpStrings.message = "success";
        }
        return snmpStrings;
    }

    public ApiIfaceData getNetworkDeviceIfaces(String login, String key, Integer id) {
        ApiIfaceData apiIfaceData = new ApiIfaceData();

        apiIfaceData.data = new ArrayList<>();
        apiIfaceData.status = 1;
        apiIfaceData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetNetworkDeviceIfaces")) {
            apiIfaceData.message = "Permission denied";
            return apiIfaceData;
        }

        NccNetworkDeviceData device = new NccNetworkDevice().getNetworkDevices(id);

        if (device != null) {

            ArrayList<IfaceData> ifaces = new NccNetworkDevice().getIfaces(id, new ArrayList<Integer>(Arrays.asList(6, 117)));

            if (ifaces != null) {
                apiIfaceData.data = ifaces;
                apiIfaceData.status = 0;
                apiIfaceData.message = "success";
            }
        }

        return apiIfaceData;
    }

    public ApiIfaceData updateNetworkDeviceIfaces(String login, String key, Integer id) {
        ApiIfaceData apiIfaceData = new ApiIfaceData();

        apiIfaceData.data = new ArrayList<>();
        apiIfaceData.status = 1;
        apiIfaceData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "UpdateNetworkDeviceIfaces")) {
            apiIfaceData.message = "Permission denied";
            return apiIfaceData;
        }

        NccNetworkDeviceData device = new NccNetworkDevice().getNetworkDevices(id);

        if (device != null) {

            ArrayList<IfaceData> ifaces = new NccNetworkDevice().updateIfaces(id);

            if (ifaces != null) {
                apiIfaceData.data = ifaces;
                apiIfaceData.status = 0;
                apiIfaceData.message = "success";
            }
        }

        return apiIfaceData;
    }

    public ApiIfaceData discoverNetworkDeviceIfaces(String login, String key, Integer id) {
        ApiIfaceData apiIfaceData = new ApiIfaceData();

        apiIfaceData.data = new ArrayList<>();
        apiIfaceData.status = 1;
        apiIfaceData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "DiscoverNetworkDeviceIfaces")) {
            apiIfaceData.message = "Permission denied";
            return apiIfaceData;
        }

        NccNetworkDeviceData device = new NccNetworkDevice().getNetworkDevices(id);

        if (device != null) {

            ArrayList<IfaceData> ifaces = new NccNetworkDevice().discoverIfaces(id);

            if (ifaces != null) {
                apiIfaceData.data = ifaces;
                apiIfaceData.status = 0;
                apiIfaceData.message = "success";
            }
        }

        return apiIfaceData;
    }

    public ApiIfaceData setIfaceState(String login, String key, Integer deviceId, Integer iface, Integer state) {
        ApiIfaceData apiIfaceData = new ApiIfaceData();

        apiIfaceData.data = new ArrayList<>();
        apiIfaceData.status = 1;
        apiIfaceData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "UpdateNetworkDeviceIfaces")) {
            apiIfaceData.message = "Permission denied";
            return apiIfaceData;
        }

        NccNetworkDeviceData device = new NccNetworkDevice().getNetworkDevices(deviceId);

        if (device != null) {

            new NccNetworkDevice().setIfaceState(deviceId, iface, state);

            apiIfaceData.status = 0;
            apiIfaceData.message = "success";
        }

        return apiIfaceData;
    }
}
