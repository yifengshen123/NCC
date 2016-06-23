package com.NccNAS;

import com.NccSystem.NccAbstractData;
import java.sql.SQLException;

public class NccNasData extends NccAbstractData<NccNasData> {
    public Integer id;
    public String nasName;
    public Long nasIP;
    public Integer nasType;
    public Integer nasStatus;
    public String nasSecret;
    public Integer nasInterimInterval;

    @Override
    public NccNasData fillData(){
        NccNasData nasData = new NccNasData();

        try {
            nasData.id = rs.getInt("id");
            nasData.nasName = rs.getString("nasName");
            nasData.nasIP = rs.getLong("nasIP");
            nasData.nasType = rs.getInt("nasType");
            nasData.nasStatus = rs.getInt("nasStatus");
            nasData.nasSecret = rs.getString("nasSecret");
            nasData.nasInterimInterval = rs.getInt("nasInterimInterval");

            return nasData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nasData;
    }
}
