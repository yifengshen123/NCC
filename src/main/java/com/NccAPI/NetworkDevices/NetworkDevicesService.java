package com.NccAPI.NetworkDevices;

import com.NccAPI.NccApiData;
import com.NccNetworkDevices.NccNetworkDeviceData;

public interface NetworkDevicesService {
    public ApiNetworkDeviceTypeData getNetworkDeviceTypes(String login, String key);

    public ApiNetworkDeviceData createNetworkDevice(String login, String key,
                                    String deviceName,
                                    String deviceIP,
                                    Integer deviceType,
                                    String snmpCommunity,
                                    String addressStreet,
                                    String addressBuild);

    public ApiNetworkDeviceData getNetworkDevices(String login, String key);

    public ApiNetworkDeviceData updateNetworkDevice(String login, String key,
                                                    Integer id,
                                                    String deviceName,
                                                    String deviceIP,
                                                    Integer deviceType,
                                                    String snmpCommunity,
                                                    String addressStreet,
                                                    String addressBuild);

    public ApiNetworkDeviceData deleteNetworkDevice(String login, String key, Integer id);
}
