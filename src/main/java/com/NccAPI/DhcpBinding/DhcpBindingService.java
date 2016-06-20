package com.NccAPI.DhcpBinding;

import com.NccDhcp.NccDhcpBindData;

public interface DhcpBindingService {

    public NccDhcpBindData getDhcpBinding(String apiKey, Integer uid);
}
