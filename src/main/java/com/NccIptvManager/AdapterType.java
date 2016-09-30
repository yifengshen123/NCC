package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

public class AdapterType extends NccAbstractData<AdapterType> {
    public Integer id;
    public String cardName;
    public String chipName;

    @Override
    public AdapterType fillData() {
        AdapterType adapterType = new AdapterType();

        try {
            adapterType.id = rs.getInt("id");
            adapterType.cardName = rs.getString("cardName");
            adapterType.chipName = rs.getString("chipName");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return adapterType;
    }
}
