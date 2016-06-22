package com.NccSessions;

import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccSessionData {
    public Integer id;
    public String sessionId;
    public Long startTime;
    public Long stopTime;
    public Long acctInputOctets;
    public Long acctOutputOctets;
    public Integer terminateCause;
    public Long lastAlive;
    public Long sessionDuration;
    public Integer nasId;
    public Long framedIP;
    public String framedMAC;
    public Integer userId;
    public Long framedAgentId;
    public String framedCircuitId;
    public String framedRemoteId;
    public Integer userTariff;

    private CachedRowSetImpl rs;

    public NccSessionData() {
    }

    NccSessionData(CachedRowSetImpl rs) throws NccSessionsException {
        this.rs = rs;
        if (rs == null) {
            throw new NccSessionsException("rs is null");
        }
    }

    private NccSessionData fillData() throws NccSessionsException {
        try {
            this.id = this.rs.getInt("id");
            this.sessionId = this.rs.getString("sessionId");
            this.startTime = this.rs.getLong("startTime");
            this.acctInputOctets = this.rs.getLong("acctInputOctets");
            this.acctOutputOctets = this.rs.getLong("acctOutputOctets");
            this.nasId = this.rs.getInt("nasId");
            this.framedIP = this.rs.getLong("framedIP");
            this.framedMAC = this.rs.getString("framedMAC");
            this.framedAgentId = this.rs.getLong("framedAgentId");
            this.framedCircuitId = this.rs.getString("framedCircuitId");
            this.framedRemoteId = this.rs.getString("framedRemoteId");
            this.userId = this.rs.getInt("userId");
            this.lastAlive = this.rs.getLong("lastAlive");
            this.sessionDuration = this.rs.getLong("sessionDuration");
            this.userTariff = this.rs.getInt("userTariff");
            return this;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NccSessionsException("rs.get error");
        }
    }

    NccSessionData getData() throws NccSessionsException {
        try {
            if (this.rs.next()) {
                return fillData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    ArrayList<NccSessionData> getDataList() throws NccSessionsException {
        ArrayList<NccSessionData> sessionDatas = new ArrayList<>();

        try {
            while (this.rs.next()) {
                sessionDatas.add(fillData());
            }
            return sessionDatas;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
