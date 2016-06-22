package com.NccNAS;

import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccNasType {
    public Integer id;
    public String typeName;

    private CachedRowSetImpl rs;

    NccNasType() {
    }

    NccNasType(CachedRowSetImpl rs) throws NccNasException {
        this.rs = rs;
        if (rs == null) {
            throw new NccNasException("rs is null");
        }
    }

    private NccNasType fillData() throws NccNasException {
        try {
            this.id = this.rs.getInt("id");
            this.typeName = this.rs.getString("typeName");
            return this;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NccNasException("rs.get error");
        }
    }

    NccNasType getData() throws NccNasException {
        try {
            if (this.rs.next()) {
                return fillData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    ArrayList<NccNasType> getDataList() throws NccNasException {
        ArrayList<NccNasType> nasTypes = new ArrayList<>();

        try {
            while (this.rs.next()) {
                nasTypes.add(fillData());
            }
            return nasTypes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
