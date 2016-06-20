package com.NccAPI.DhcpBinding;

import com.NccDhcp.NccDhcpBindData;
import com.NccDhcp.NccDhcpUnbindedData;

import java.util.ArrayList;

public interface DhcpBindingService {

    public NccDhcpBindData getDhcpBinding(String apiKey, Integer uid);
    public ArrayList<NccDhcpBindData> getDhcpBinding(String apiKey);

    public void setDhcpUnbinded(String apiKey, String remoteID, String circuitID, String clientMAC, Long relayAgent);

    public void clearDhcpBinding(String apiKey, Integer uid);

    public NccDhcpUnbindedData getDhcpUnbinded(String apiKey, Integer id);
    public ArrayList<NccDhcpUnbindedData> getDhcpUnbinded(String apiKey);

    public void clearDhcpUnbinded(String apiKey, Integer id);

    public Integer setDhcpBinding(String apiKey, Integer uid, String remoteID, String circuitID, String clientMAC, Long relayAgent);
}
