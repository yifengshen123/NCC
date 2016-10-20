package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 20.10.16.
 */
public class SatData extends NccAbstractData<SatData> {
    public Integer id;
    public String satName;
    public String satDeg;

    @Override
    public SatData fillData(){
        SatData satData = new SatData();

        try {
            satData.id = rs.getInt("id");
            satData.satName = rs.getString("satName");
            satData.satDeg = rs.getString("satDeg");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return satData;
    }
}
