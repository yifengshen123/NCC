package com.NccNAS;

import com.NccSystem.NccAbstractData;
import java.sql.SQLException;

public class NccNasType extends NccAbstractData<NccNasType> {
    public Integer id;
    public String typeName;

    public NccNasType fillData(){
        NccNasType nasType = new NccNasType();

        try {
            nasType.id = rs.getInt("id");
            nasType.typeName = rs.getString("typeName");

            return nasType;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nasType;
    }
}
