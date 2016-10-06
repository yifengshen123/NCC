package com.NccNetworkDevices;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 05.10.16.
 */
public class NccNetworkDeviceType extends NccAbstractData<NccNetworkDeviceType> {
    public Integer id;
    public String typeName;
    public Integer mibSet;

    @Override
    public NccNetworkDeviceType fillData(){
        NccNetworkDeviceType type = new NccNetworkDeviceType();

        try {
            type.id = rs.getInt("id");
            type.typeName = rs.getString("typeName");
            type.mibSet = rs.getInt("mibSet");

            return type;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
