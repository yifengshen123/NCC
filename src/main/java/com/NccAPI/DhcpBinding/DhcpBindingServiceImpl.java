package com.NccAPI.DhcpBinding;

import com.NccAPI.NccAPI;
import com.NccDhcp.NccDhcpBindData;
import com.NccDhcp.NccDhcpBinding;
import com.NccDhcp.NccDhcpServer;
import com.NccDhcp.NccDhcpUnbindedData;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class DhcpBindingServiceImpl implements DhcpBindingService {

    private static Logger logger = Logger.getLogger(DhcpBindingService.class);

    public NccDhcpBindData getDhcpBinding(String apiKey, Integer uid) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        NccDhcpBindData bindData = new NccDhcpBinding().getBinding(uid);

        if (bindData != null) {
            return bindData;
        }

        return null;
    }

    public ArrayList<NccDhcpBindData> getDhcpBinding(String apiKey) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        ArrayList<NccDhcpBindData> binding = new NccDhcpBinding().getBinding();

        if (binding != null) {
            return binding;
        }

        return null;
    }

    public Integer setDhcpBinding(String apiKey, Integer uid, String remoteID, String circuitID, String clientMAC, Long relayAgent) {

        Integer id = new NccDhcpBinding().setBinding(uid, remoteID, circuitID, clientMAC, relayAgent);

        if (id != null) {
            return id;
        }

        return null;
    }

    public void clearDhcpUnbinded(String apiKey, Integer id) {
        if (!new NccAPI().checkKey(apiKey)) return;

        new NccDhcpBinding().clearUnbinded(id);
    }

    public NccDhcpUnbindedData getDhcpUnbinded(String apiKey, Integer id) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        NccDhcpUnbindedData unbindedData = new NccDhcpBinding().getUnbinded(id);

        if (unbindedData != null) {
            return unbindedData;
        }

        return null;
    }

    public ArrayList<NccDhcpUnbindedData> getDhcpUnbinded(String apiKey) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        ArrayList<NccDhcpUnbindedData> unbinded = new NccDhcpBinding().getUnbinded();

        if (unbinded != null) {
            return unbinded;
        }

        return null;
    }

    public void setDhcpUnbinded(String apiKey, String remoteID, String circuitID, String clientMAC, Long relayAgent) {
        if (!new NccAPI().checkKey(apiKey)) return;

        new NccDhcpBinding().setUnbinded(remoteID, circuitID, clientMAC, relayAgent);
    }

    public void clearDhcpBinding(String apiKey, Integer uid) {
        if (!new NccAPI().checkKey(apiKey)) return;

        new NccDhcpBinding().clearBinding(uid);
    }
}
