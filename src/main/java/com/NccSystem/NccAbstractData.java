package com.NccSystem;

import com.NccSystem.SQL.NccCachedRowset;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.util.ArrayList;

public abstract class NccAbstractData<T> {

    protected NccCachedRowset rs;

    public NccAbstractData() {
    }

    public abstract T fillData();

    public T getData(String queryString) {

        try {
            NccQuery query = new NccQuery();

            rs = query.selectQuery(queryString);

            if (rs != null) {
                try {
                    if (rs.next()) {
                        return fillData();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<T> getDataList(String queryString) {
        ArrayList<T> datas = new ArrayList<T>();

        try {
            NccQuery query = new NccQuery();

            rs = query.selectQuery(queryString);

            if (rs != null) {
                try {
                    while (rs.next()) {
                        datas.add(fillData());
                    }

                    return datas;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return datas;
    }
}
