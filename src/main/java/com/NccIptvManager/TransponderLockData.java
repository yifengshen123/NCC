package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 24.10.16.
 */
public class TransponderLockData extends NccAbstractData<TransponderLockData> {

    public Integer id;
    public Integer transponderId;
    public Long lastLock;
    public Integer signal;
    public Integer snr;
    public Integer ber;
    public Integer unc;

    public TransponderLockData(){

    }

    public TransponderLockData(Integer transponderId, Integer signal, Integer snr, Integer ber, Integer unc){
        this.transponderId = transponderId;
        this.signal = signal;
        this.snr = snr;
        this.ber = ber;
        this.unc = unc;
    }

    @Override
    public TransponderLockData fillData(){

        TransponderLockData transponderLockData = new TransponderLockData();

        try {
            transponderLockData.id = rs.getInt("id");
            transponderLockData.transponderId = rs.getInt("transponderId");
            transponderLockData.lastLock = rs.getLong("lastLock");
            transponderLockData.signal = rs.getInt("signal");
            transponderLockData.snr = rs.getInt("snr");
            transponderLockData.ber = rs.getInt("ber");
            transponderLockData.unc = rs.getInt("unc");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transponderLockData;
    }
}
