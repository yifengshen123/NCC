package com.NccDhcp;

import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccDhcpUnbindedData {
    public Integer id = 0;
    public String remoteID = "";
    public String circuitID = "";
    public String clientMAC = "";
    public Long relayAgent = 0L;

    private CachedRowSetImpl rs = null;

    NccDhcpUnbindedData() {
    }

    NccDhcpUnbindedData(CachedRowSetImpl rs) throws NccDhcpException {
        this.rs = rs;
        if (rs == null) {
            throw new NccDhcpException("rs is null");
        }
    }

    private NccDhcpUnbindedData fillData() throws NccDhcpException {
        try {
            this.id = this.rs.getInt("id");
            this.remoteID = this.rs.getString("remoteID");
            this.circuitID = this.rs.getString("circuitID");
            this.clientMAC = this.rs.getString("clientMAC");
            this.relayAgent = this.rs.getLong("relayAgent");

            return this;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NccDhcpException("rs.get error");
        }
    }

    NccDhcpUnbindedData getData() throws NccDhcpException {
        try {
            if (this.rs.next()) {
                return fillData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    ArrayList<NccDhcpUnbindedData> getDataList() throws NccDhcpException {
        ArrayList<NccDhcpUnbindedData> unbindedDatas = new ArrayList<>();

        try {
            while (this.rs.next()) {
                unbindedDatas.add(fillData());
            }
            return unbindedDatas;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
