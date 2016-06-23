package com.NccDhcp;

import com.NccSystem.NccAbstractData;
import java.sql.SQLException;

public class NccDhcpBindData extends NccAbstractData<NccDhcpBindData> {
    public Integer id = 0;
    public Integer uid = 0;
    public String remoteID = "";
    public String circuitID = "";
    public String clientMAC = "";
    public Long relayAgent = 0L;

    @Override
    public NccDhcpBindData fillData(){
        NccDhcpBindData bindData = new NccDhcpBindData();

        try {
            bindData.id = rs.getInt("id");
            bindData.uid = rs.getInt("uid");
            bindData.remoteID = rs.getString("remoteID");
            bindData.circuitID = rs.getString("circuitID");
            bindData.clientMAC = rs.getString("clientMAC");
            bindData.relayAgent = rs.getLong("relayAgent");

            return bindData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bindData;
    }
}
