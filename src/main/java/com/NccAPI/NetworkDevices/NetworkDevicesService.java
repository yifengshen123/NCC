package com.NccAPI.NetworkDevices;

import com.NccDhcp.NccDhcpRelayAgentData;
import com.NccDhcp.NccDhcpRelayAgentType;

import java.util.ArrayList;

public interface NetworkDevicesService {
    public ApiNetworkDeviceTypeData getNetworkDeviceTypes(String login, String key);

    public void createNetworkDevice(String login, String key,
                                    String deviceName,
                                    String deviceIP,
                                    Integer deviceType,
                                    String snmpCommunity,
                                    String addressStreet,
                                    String addressBuild);

    public ApiNetworkDeviceData getNetworkDevices(String login, String key);
}
