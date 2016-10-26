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
    public Integer ifSpeed;
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

    @Override
    public IfaceData fillData(){
        IfaceData ifaceData = new IfaceData();

        try {
            ifaceData.ifIndex = rs.getInt("ifIndex");
            ifaceData.ifType = rs.getInt("ifType");
            ifaceData.ifDescr = rs.getString("ifDescr");
            ifaceData.ifSpeed = rs.getInt("ifSpeed");
            ifaceData.ifOperStatus = rs.getInt("ifOperStatus");
            ifaceData.ifAdminStatus = rs.getInt("ifAdminStatus");
            ifaceData.ifInOctets = rs.getLong("ifInOctets");
            ifaceData.ifOutOctets = rs.getLong("ifOutOctets");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ifaceData;
    }

}
