package com.NccPools;

import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import java.util.ArrayList;

public class NccPools {

    private NccQuery query;

    public NccPools() {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public NccPoolData getPool(Integer id) {

        return new NccPoolData().getData("SELECT * FROM nccPools WHERE id=" + id);
    }

    public ArrayList<NccPoolData> getPool() {

        return new NccPoolData().getDataList("SELECT * FROM nccPools");
    }

}
