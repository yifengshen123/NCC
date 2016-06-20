package com.NccAPI.DhcpBinding;

import com.NccAPI.NccAPI;
import com.NccDhcp.NccDhcpBindData;
import com.NccDhcp.NccDhcpBinding;
import com.NccDhcp.NccDhcpServer;
import org.apache.log4j.Logger;

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
}
