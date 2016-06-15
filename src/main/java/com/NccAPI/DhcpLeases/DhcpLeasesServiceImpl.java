package com.NccAPI.DhcpLeases;

import com.NccAPI.NccAPI;
import com.NccDhcp.NccDhcpException;
import com.NccDhcp.NccDhcpLeaseData;
import com.NccDhcp.NccDhcpLeases;

import java.util.ArrayList;

public class DhcpLeasesServiceImpl implements DhcpLeasesService {

    public NccDhcpLeaseData getLeaseByUID(String apiKey, Integer uid) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        try {
            NccDhcpLeaseData leaseData = new NccDhcpLeases().getLeaseByUid(uid);

            if (leaseData != null) {
                return leaseData;
            }
        } catch (NccDhcpException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccDhcpLeaseData getLeases(String apiKey, Integer id) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        try {
            NccDhcpLeaseData leaseData = new NccDhcpLeases().getLeases(id);

            if (leaseData != null) {
                return leaseData;
            }
        } catch (NccDhcpException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<NccDhcpLeaseData> getLeases(String apiKey) {

        try {
            ArrayList<NccDhcpLeaseData> leases = new NccDhcpLeases().getLeases();

            if (leases != null) {
                return leases;
            }
        } catch (NccDhcpException e) {
            e.printStackTrace();
        }

        return null;
    }
}
