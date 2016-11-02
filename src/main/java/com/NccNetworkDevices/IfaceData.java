package com.NccNetworkDevices;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by root on 26.10.16.
 */
public class IfaceData extends NccAbstractData<IfaceData> {
    public Integer ifIndex;
    public Integer ifType;
    public String ifDescr;
    public Long ifSpeed;
    public Integer ifAdminStatus;
    public Integer ifOperStatus;
    public Long ifInOctets;
    public Long ifOutOctets;
    public Long ifInErrors;
    public Long ifOutErrors;
    public Long ifInUcastPkts;
    public Long ifOutUcastPkts;
    public Long ifInDiscards;
    public Long ifOutDiscards;
    public Long ifInNUcastPkts;
    public Long ifOutNUcastPkts;
    public String ifPhysAddress;
    public Long ifHCInOctets;
    public Long ifHCOutOctets;
    public Long lastUpdate;

    @Override
    public IfaceData fillData(){
        IfaceData ifaceData = new IfaceData();

        try {
            ifaceData.ifIndex = rs.getInt("ifIndex");
            ifaceData.ifType = rs.getInt("ifType");
            ifaceData.ifDescr = rs.getString("ifDescr");
            ifaceData.ifSpeed = rs.getLong("ifSpeed");
            ifaceData.ifOperStatus = rs.getInt("ifOperStatus");
            ifaceData.ifAdminStatus = rs.getInt("ifAdminStatus");
            ifaceData.ifInOctets = rs.getLong("ifInOctets");
            ifaceData.ifOutOctets = rs.getLong("ifOutOctets");
            ifaceData.ifInUcastPkts = rs.getLong("ifInUcastPkts");
            ifaceData.ifOutUcastPkts = rs.getLong("ifOutUcastPkts");
            ifaceData.ifInErrors = rs.getLong("ifInErrors");
            ifaceData.ifOutErrors = rs.getLong("ifOutErrors");
            ifaceData.ifInNUcastPkts = rs.getLong("ifInNUcastPkts");
            ifaceData.ifOutNUcastPkts = rs.getLong("ifOutNUcastPkts");
            ifaceData.ifPhysAddress = rs.getString("ifPhysAddress");
            ifaceData.ifHCInOctets = rs.getLong("ifHCInOctets");
            ifaceData.ifHCOutOctets = rs.getLong("ifHCOutOctets");
            ifaceData.lastUpdate = rs.getLong("lastUpdate");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ifaceData;
    }

}
