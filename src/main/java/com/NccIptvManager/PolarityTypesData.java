package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 20.10.16.
 */
public class PolarityTypesData extends NccAbstractData<PolarityTypesData> {
    public Integer id;
    public String polarity;

    @Override
    public PolarityTypesData fillData(){
        PolarityTypesData polarityTypesData = new PolarityTypesData();

        try {
            polarityTypesData.id = rs.getInt("id");
            polarityTypesData.polarity = rs.getString("polarity");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return polarityTypesData;
    }
}
