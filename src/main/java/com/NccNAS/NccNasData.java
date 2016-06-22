package com.NccNAS;

import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccNasData {
    public Integer id;
    public String nasName;
    public Long nasIP;
    public Integer nasType;
    public Integer nasStatus;
    public String nasSecret;
    public Integer nasInterimInterval;

    private CachedRowSetImpl rs;

    public NccNasData() {
    }

    NccNasData(CachedRowSetImpl rs) throws NccNasException {
        this.rs = rs;
        if (rs == null) {
            throw new NccNasException("rs is null");
        }
    }

    private NccNasData fillData() throws NccNasException {
        try {
            this.id = this.rs.getInt("id");
            this.nasName = this.rs.getString("nasName");
            this.nasIP = this.rs.getLong("nasIP");
            this.nasType = this.rs.getInt("nasType");
            this.nasStatus = this.rs.getInt("nasStatus");
            this.nasSecret = this.rs.getString("nasSecret");
            this.nasInterimInterval = this.rs.getInt("nasInterimInterval");
            return this;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NccNasException("rs.get error");
        }
    }

    NccNasData getData() throws NccNasException {
        try {
            if (this.rs.next()) {
                return fillData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    ArrayList<NccNasData> getDataList() throws NccNasException {
        ArrayList<NccNasData> nasDatas = new ArrayList<>();

        try {
            while (this.rs.next()) {
                nasDatas.add(fillData());
            }
            return nasDatas;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
