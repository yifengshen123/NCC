package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 20.10.16.
 */
public class SymbolRatesData extends NccAbstractData<SymbolRatesData> {
    public Integer id;
    public Integer rate;

    @Override
    public SymbolRatesData fillData(){
        SymbolRatesData symbolRatesData = new SymbolRatesData();

        try {
            symbolRatesData.id = rs.getInt("id");
            symbolRatesData.rate = rs.getInt("rate");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return symbolRatesData;
    }
}
