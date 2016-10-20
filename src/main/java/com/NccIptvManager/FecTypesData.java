package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 20.10.16.
 */
public class FecTypesData extends NccAbstractData<FecTypesData> {
    public Integer id;
    public String fec;

    @Override
    public FecTypesData fillData(){
        FecTypesData fecTypesData = new FecTypesData();

        try {
            fecTypesData.id = rs.getInt("id");
            fecTypesData.fec = rs.getString("fec");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fecTypesData;
    }
}
