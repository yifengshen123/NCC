package com.NccAPI.NetworkDevices;

public interface NetworkDevicesService {
    ApiIfaceData setIfaceState(String login, String key, Integer deviceId, Integer iface, Integer state);

    ApiIfaceData getNetworkDeviceIfaces(String login, String key, Integer id);

    ApiIfaceData updateNetworkDeviceIfaces(String login, String key, Integer id);

    ApiIfaceData discoverNetworkDeviceIfaces(String login, String key, Integer id);

    ApiNetworkDeviceTypeData getNetworkDeviceTypes(String login, String key);

    ApiNetworkDeviceData createNetworkDevice(String login, String key,
                                             String deviceName,
                                             String deviceIP,
                                             Integer deviceType,
                                             String snmpCommunity,
                                             String addressStreet,
                                             String addressBuild);

    ApiNetworkDeviceData getNetworkDevices(String login, String key);

    ApiNetworkDeviceData updateNetworkDevice(String login, String key,
                                             Integer id,
                                             String deviceName,
                                             String deviceIP,
                                             Integer deviceType,
                                             String snmpCommunity,
                                             String addressStreet,
                                             String addressBuild);

    ApiNetworkDeviceData deleteNetworkDevice(String login, String key, Integer id);

    ApiSnmpString getNetworkDeviceSnmpValue(String login, String key, Integer id, String oid);

    ApiSnmpStrings getNetworkDeviceSnmpValues(String login, String key, Integer id, String oid);
}
