package com.NccDhcp;

import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccDhcpBindData {
    public Integer id = 0;
    public Integer uid = 0;
    public String remoteID = "";
    public String circuitID = "";
    public String clientMAC = "";
    public Long relayAgent = 0L;

    private CachedRowSetImpl rs = null;

    NccDhcpBindData() {
    }

    NccDhcpBindData(CachedRowSetImpl rs) throws NccDhcpException {
        this.rs = rs;
        if (rs == null) {
            throw new NccDhcpException("rs is null");
        }
    }

    private NccDhcpBindData fillData() throws NccDhcpException {
        try {
            this.id = this.rs.getInt("id");
            this.uid = this.rs.getInt("uid");
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

    NccDhcpBindData getData() throws NccDhcpException {
        try {
            if(this.rs.next()){
                return fillData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    ArrayList<NccDhcpBindData> getDataList() throws NccDhcpException {
        ArrayList<NccDhcpBindData> bindDatas = new ArrayList<>();

        try {
            while (this.rs.next()){
                bindDatas.add(fillData());
            }
            return bindDatas;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
