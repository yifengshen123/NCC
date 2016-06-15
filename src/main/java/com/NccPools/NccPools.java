package com.NccPools;

import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccPools {

    private NccQuery query;

    public NccPools(){
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    private NccPoolData fillPoolData(CachedRowSetImpl rs){
        NccPoolData poolData = new NccPoolData();

        try {
            poolData.id = rs.getInt("id");
            poolData.poolName = rs.getString("poolName");
            poolData.poolStart = rs.getLong("poolStart");
            poolData.poolEnd = rs.getLong("poolEnd");
            poolData.poolRouter = rs.getLong("poolRouter");
            poolData.poolNetmask  = rs.getLong("poolNetmask");
            poolData.poolDNS1 = rs.getLong("poolDNS1");
            poolData.poolDNS2 = rs.getLong("poolDNS2");
            poolData.poolNextServer = rs.getLong("poolNextServer");
            poolData.poolLeaseTime = rs.getInt("poolLeaseTime");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return poolData;
    }

    public NccPoolData getPool(Integer id) {

        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccPools WHERE id=" + id);

            if(rs != null){

                try {
                    if(rs.next()){
                        return fillPoolData(rs);
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

    public ArrayList<NccPoolData> getPool() {

        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccPools");

            if(rs != null){
                ArrayList<NccPoolData> pools = new ArrayList<>();

                try {
                    while(rs.next()){
                        NccPoolData poolData = fillPoolData(rs);
                        if(poolData!=null){
                            pools.add(poolData);
                        }
                    }

                    return pools;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

}
