package com.NccAPI.DhcpLeases;

import com.NccDhcp.NccDhcpLeaseData;

import java.util.ArrayList;

public interface DhcpLeasesService {
    public NccDhcpLeaseData getLeases(String apiKey, Integer id);
    public ArrayList<NccDhcpLeaseData> getLeases(String apiKey);

    public NccDhcpLeaseData getLeaseByUID(String apiKey, Integer uid);
}
