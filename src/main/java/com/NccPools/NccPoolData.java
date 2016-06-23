package com.NccPools;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

public class NccPoolData extends NccAbstractData<NccPoolData> {
    public Integer id;
    public String poolName;
    public Long poolStart;
    public Long poolEnd;
    public Long poolRouter;
    public Long poolNetmask;
    public Long poolDNS1;
    public Long poolDNS2;
    public Long poolNextServer;
    public Integer poolLeaseTime;

    @Override
    public NccPoolData fillData(){
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

            return poolData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return poolData;
    }
}
