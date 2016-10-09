package com.NccAPI.NetworkDevices;

import com.NccAPI.NccApiData;
import com.NccAPI.NccAPI;
import com.NccNetworkDevices.NccNetworkDevice;
import com.NccNetworkDevices.NccNetworkDeviceData;
import com.NccNetworkDevices.NccNetworkDeviceType;
import com.NccSystem.NccUtils;

import java.util.ArrayList;

public class NetworkDevicesServiceImpl implements NetworkDevicesService {
    public ApiNetworkDeviceTypeData getNetworkDeviceTypes(String login, String key) {

        ApiNetworkDeviceTypeData typeData = new ApiNetworkDeviceTypeData();
        typeData.data = new ArrayList<NccNetworkDeviceType>();
        typeData.status = 1;
        typeData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetNetworkDeviceTypes")){
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

        if (!new NccAPI().checkPermission(login, key, "CreateNetworkDevice")){
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

        new NccNetworkDevice().createDevice(device);

        return deviceData;
    }

    public ApiNetworkDeviceData getNetworkDevices(String login, String key) {

        ApiNetworkDeviceData deviceData = new ApiNetworkDeviceData();
        deviceData.data = new ArrayList<NccNetworkDeviceData>();
        deviceData.status = 1;
        deviceData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "GetNetworkDevices")){
            deviceData.message = "Permission denied";
            return deviceData;
        };

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

    public ApiNetworkDeviceData deleteNetworkDevice(String login, String key, Integer id) {
        ApiNetworkDeviceData deviceData = new ApiNetworkDeviceData();

        deviceData.data = new ArrayList<NccNetworkDeviceData>();
        deviceData.status = 1;
        deviceData.message = "error";

        if (!new NccAPI().checkPermission(login, key, "DeleteNetworkDevice")){
            deviceData.message = "Permission denied";
            return deviceData;
        }
        new NccNetworkDevice().deleteDevice(id);
        deviceData.status = 0;
        deviceData.message = "success";
        return deviceData;
    }
}
