package com.NccDhcp;

import com.NccSystem.NccAbstractData;
import java.sql.SQLException;

public class NccDhcpUnbindedData extends NccAbstractData<NccDhcpUnbindedData> {
    public Integer id = 0;
    public String remoteID = "";
    public String circuitID = "";
    public String clientMAC = "";
    public Long relayAgent = 0L;

    @Override
    public NccDhcpUnbindedData fillData(){
        NccDhcpUnbindedData unbindedData = new NccDhcpUnbindedData();

        try {
            unbindedData.id = rs.getInt("id");
            unbindedData.remoteID = rs.getString("remoteID");
            unbindedData.circuitID = rs.getString("circuitID");
            unbindedData.clientMAC = rs.getString("clientMAC");
            unbindedData.relayAgent = rs.getLong("relayAgent");

            return unbindedData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unbindedData;
    }
}
