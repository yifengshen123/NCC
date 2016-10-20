package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 20.10.16.
 */
public class LnbTypesData extends NccAbstractData<LnbTypesData> {
    public Integer id;
    public String lnb;

    @Override
    public LnbTypesData fillData(){
        LnbTypesData lnbTypesData = new LnbTypesData();

        try {
            lnbTypesData.id = rs.getInt("id");
            lnbTypesData.lnb = rs.getString("lnb");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lnbTypesData;
    }
}
