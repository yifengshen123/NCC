package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 20.10.16.
 */
public class TransponderTypesData extends NccAbstractData<TransponderTypesData> {
    public Integer id;
    public String type;

    @Override
    public TransponderTypesData fillData(){
        TransponderTypesData transponderTypesData = new TransponderTypesData();

        try {
            transponderTypesData.id = rs.getInt("id");
            transponderTypesData.type = rs.getString("type");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transponderTypesData;
    }
}
