package com.NccTariffScale;

import com.NccPools.NccPoolData;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccTariffScale {

    private static Logger logger = Logger.getLogger(NccTariffScale.class);
    private NccQuery query;

    public NccTariffScale() {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public RateData getRate(Integer id) {

        CachedRowSetImpl rs;

        try {

            rs = query.selectQuery("SELECT " +
                    "in_speed, " +
                    "out_speed " +
                    "FROM tarif_plans t " +
                    "LEFT JOIN intervals i ON i.tp_id=t.tp_id " +
                    "LEFT JOIN trafic_tarifs tt ON tt.interval_id=i.id " +
                    "WHERE t.id=" + id);

            if (rs != null) {
                try {
                    if(rs.next()){
                        RateData rateData = new RateData();
                        rateData.inRate = rs.getInt("in_speed");
                        rateData.outRate = rs.getInt("out_speed");

                        return rateData;
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

    public TariffData getTariffByUserId(Integer userId) {

        try {
            NccQuery query = new NccQuery();
            CachedRowSetImpl rs;

            rs = query.selectQuery("SELECT * FROM nccViewUserTariff WHERE userId=" + userId);

            if (rs != null) {
                try {
                    if (rs.next()) {
                        TariffData tariffData = new TariffData();

                        tariffData.id = rs.getInt("id");
                        tariffData.tariffName = rs.getString("tariffName");
                        tariffData.tariffCost = rs.getDouble("tariffCost");
                        tariffData.tariffStart = rs.getDate("tariffStart");
                        tariffData.tariffEnd = rs.getDate("tariffEnd");
                        tariffData.tariffStatus = rs.getInt("tariffStatus");

                        return tariffData;
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

    public ArrayList<NccPoolData> getTariffPools(Integer tariffId) {
        try {
            NccQuery query = new NccQuery();
            CachedRowSetImpl rs;

            rs = query.selectQuery("SELECT * FROM nccViewTariffPools WHERE tariffId=" + tariffId);

            if (rs != null) {
                try {
                    ArrayList<NccPoolData> tariffPools = new ArrayList<>();

                    while (rs.next()) {
                        NccPoolData poolData = new NccPoolData();

                        poolData.id = rs.getInt("poolId");
                        poolData.poolName = rs.getString("poolName");
                        poolData.poolStart = rs.getLong("poolStart");
                        poolData.poolEnd = rs.getLong("poolEnd");

                        tariffPools.add(poolData);
                    }

                    return tariffPools;
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
